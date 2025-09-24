package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.databind.DataBinding
import io.ktor.client.HttpClient
import kotlinx.serialization.Serializable

class AccountsClient(
    config: SchwabClientConfig,
    client: HttpClient,
    oathToken: DataBinding<String?> = DataBinding(null)
) : BaseClient(config, client, oathToken, listOf("trader", "v1")) {
    fun listAccountNumbers(): ApiResponse<List<GetAccountNumberResponse>> =
        getAt("accounts", "accountNumbers")
}

@Serializable
data class GetAccountNumberResponse(
    val accountNumber: String,
    val hashValue: String,
)
