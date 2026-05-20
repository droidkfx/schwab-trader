#!/usr/bin/env python3
"""
Fetch local snapshots of Schwab Trader API OpenAPI specifications.

Fetches all known APIs by default and saves each spec as a separate JSON file
in this directory. IntelliJ can preview them directly as Swagger docs.

HOW TO GET YOUR TOKEN
---------------------
1. Log in to https://developer.schwab.com in Chrome/Edge/Firefox.
2. Open DevTools (F12) -> Network tab.
3. Navigate to any API docs page so the spec request fires.
4. Find a request to jfk2-api-gateway.schwab.com, right-click it,
   and choose "Copy > Copy as cURL".
5. In the cURL command, copy the value after "authorization: Bearer ".
6. Save it (just the token, no "Bearer " prefix) to:
       local-api/.schwab-token

USAGE
-----
    # Fetch all known APIs (default)
    uv run python local-api/fetch-schwab-spec.py

    # Fetch a specific API by name (substring match, case-insensitive)
    uv run python local-api/fetch-schwab-spec.py --api trader
    uv run python local-api/fetch-schwab-spec.py --api "market data"

    # Pass token inline instead of using the file
    uv run python local-api/fetch-schwab-spec.py --token "I0.b2F1dG..."

    # Dump raw API response for debugging (saved alongside the spec)
    uv run python local-api/fetch-schwab-spec.py --dump-raw

ADDING A NEW API
----------------
Add an entry to the APIS list below with the name as it appears in the
Schwab portal and a short output filename.
"""

import argparse
import json
import sys
import urllib.error
import urllib.request
import uuid
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path
from urllib.parse import quote

# ---------------------------------------------------------------------------
# Paths
# ---------------------------------------------------------------------------

SCRIPT_DIR = Path(__file__).parent
TOKEN_FILE = SCRIPT_DIR / ".schwab-token"

# ---------------------------------------------------------------------------
# Known APIs
# Extend this list when Schwab adds new API products to the portal.
# ---------------------------------------------------------------------------

BASE_URL = (
    "https://jfk2-api-gateway.schwab.com/api/DevPortalV3.DevPortalExperienceApi"
    "/v1/DevPortalExperience/api/v1/api-specification"
)

APIS = [
    {"name": "Retail Trader API Production", "output": "schwab-trader-api-spec.json"},
    {"name": "Market Data Production", "output": "schwab-market-data-spec.json"},
]

HTTP_METHODS = frozenset(("get", "post", "put", "patch", "delete", "head", "options"))

# ---------------------------------------------------------------------------
# Spec extraction
# ---------------------------------------------------------------------------

# Field names searched in order when unwrapping the portal response.
# "specification" is the known primary field in Schwab's gateway API.
WRAPPER_FIELDS = (
    "specification",
    "body", "content", "spec", "data",
    "apiSpec", "openApiSpec", "definition", "schema", "payload",
)


def looks_like_openapi(obj: dict) -> bool:
    return "openapi" in obj or "swagger" in obj


def extract_spec(value):
    """
    Recursively unwrap `value` to find an OpenAPI/Swagger object.
    Handles plain dicts and JSON-encoded strings.
    Returns the spec dict or None.
    """
    if isinstance(value, str):
        value = value.strip()
        if not value.startswith("{"):
            return None
        try:
            value = json.loads(value)
        except json.JSONDecodeError:
            return None

    if not isinstance(value, dict):
        return None

    if looks_like_openapi(value):
        return value

    for field in WRAPPER_FIELDS:
        if field in value:
            result = extract_spec(value[field])
            if result is not None:
                return result

    return None


# ---------------------------------------------------------------------------
# HTTP
# ---------------------------------------------------------------------------

def build_headers(token: str | None) -> dict:
    return {
        "accept": "application/json",
        "accept-language": "en-US,en;q=0.9",
        "content-type": "application/json",
        "origin": "https://developer.schwab.com",
        "referer": "https://developer.schwab.com/",
        "schwab-client-appid": "AD00007720",
        "schwab-client-channel": "GW",
        "schwab-client-correlid": str(uuid.uuid4()),
        "schwab-client-env": "PROD",
        "schwab-gateway-scope": "update",
        "schwab-resource-version": "1",
        "user-agent": (
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            "AppleWebKit/537.36 (KHTML, like Gecko) "
            "Chrome/148.0.0.0 Safari/537.36"
        ),
        **({"authorization": f"Bearer {token}"} if token else {}),
    }


def fetch_url(url: str, headers: dict) -> bytes:
    req = urllib.request.Request(url, headers=headers)
    with urllib.request.urlopen(req, timeout=30) as resp:
        return resp.read()


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def load_token(token_file: Path) -> str | None:
    if not token_file.exists():
        print(
            f"Warning: {token_file} not found and --token not provided.\n"
            "Proceeding without Authorization -- expect 401/403."
        )
        return None
    token = token_file.read_text(encoding="utf-8").strip() or None
    if token:
        print(f"Using token from {token_file}")
    else:
        print(f"Warning: {token_file} is empty -- requests will likely fail.")
    return token


def summarize_spec(spec: dict) -> str:
    info = spec.get("info", {})
    title = info.get("title", "unknown")
    version = info.get("version", "?")
    paths = spec.get("paths", {})
    op_count = sum(
        len(HTTP_METHODS & set(methods))
        for methods in paths.values()
        if methods
    )
    return f'"{title}" v{version} -- {len(paths)} paths, {op_count} operations'


def fetch_api(api: dict, headers: dict, dump_raw: bool) -> bool:
    """Fetch one API, extract its spec, and save it. Returns True on success."""
    name = api["name"]
    url = f"{BASE_URL}/{quote(name)}"
    output_path = SCRIPT_DIR / api["output"]

    print(f"\n[{name}]")
    print(f"  GET {url}")

    try:
        raw_bytes = fetch_url(url, headers)
    except urllib.error.HTTPError as e:
        print(f"  HTTP {e.code} {e.reason}")
        if e.code == 401:
            print("  -> Token is missing or expired. Refresh local-api/.schwab-token.")
        return False
    except Exception as e:
        print(f"  Error: {e}")
        return False

    print(f"  Received {len(raw_bytes):,} bytes")

    try:
        outer = json.loads(raw_bytes)
    except (UnicodeDecodeError, json.JSONDecodeError) as e:
        print(f"  Could not parse response as JSON: {e}")
        if dump_raw:
            raw_dump_path = output_path.with_name(output_path.stem + ".raw.json")
            raw_dump_path.write_bytes(raw_bytes)
            print(f"  Raw bytes saved to {raw_dump_path.name}")
        return False

    if dump_raw:
        raw_dump_path = output_path.with_name(output_path.stem + ".raw.json")
        raw_dump_path.write_text(json.dumps(outer, indent=2), encoding="utf-8")
        print(f"  Raw response saved to {raw_dump_path.name}")

    spec = extract_spec(outer)
    if spec is None:
        print(
            "  [FAIL] No OpenAPI/Swagger object found in response.\n"
            "  Run with --dump-raw to inspect the structure."
        )
        return False

    output_path.write_text(json.dumps(spec, indent=2), encoding="utf-8")
    print(f"  Saved -> {output_path.name}")
    print(f"  {summarize_spec(spec)}")
    return True


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main():
    parser = argparse.ArgumentParser(
        description="Fetch local snapshots of Schwab API specs.",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=__doc__,
    )
    parser.add_argument(
        "--api",
        metavar="NAME",
        help="Fetch only the API whose name contains NAME (case-insensitive). "
             "Omit to fetch all.",
    )
    parser.add_argument(
        "--token",
        metavar="BEARER_TOKEN",
        help="Bearer token (without 'Bearer ' prefix). Overrides .schwab-token.",
    )
    parser.add_argument(
        "--token-file",
        default=str(TOKEN_FILE),
        metavar="FILE",
        help=f"File containing the Bearer token (default: {TOKEN_FILE})",
    )
    parser.add_argument(
        "--dump-raw",
        action="store_true",
        help="Save raw API responses alongside specs for debugging.",
    )
    args = parser.parse_args()

    token = args.token or load_token(Path(args.token_file))

    if args.api:
        needle = args.api.lower()
        apis = [a for a in APIS if needle in a["name"].lower()]
        if not apis:
            names = ", ".join(f'"{a["name"]}"' for a in APIS)
            print(f'No API matched "{args.api}". Known APIs: {names}')
            sys.exit(1)
    else:
        apis = APIS

    headers = build_headers(token)

    with ThreadPoolExecutor(max_workers=len(apis)) as pool:
        futures = [pool.submit(fetch_api, api, headers, args.dump_raw) for api in apis]
        results = [f.result() for f in futures]

    ok = sum(results)
    print(f"\n{ok}/{len(results)} spec(s) saved successfully.")
    if ok < len(results):
        sys.exit(1)


if __name__ == "__main__":
    main()
