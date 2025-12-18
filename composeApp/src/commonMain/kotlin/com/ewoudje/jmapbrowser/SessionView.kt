package com.ewoudje.jmapbrowser

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ewoudje.jmapbrowser.session.JMapSession
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SessionView(session: SessionViewModel) {

    PermanentNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))

                    Row {
                        Text(
                            session.username,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(Modifier.fillMaxWidth().weight(1f))

                        AccountDropdown(session)
                    }

                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))

                    for (capability in session.selectedAccount?.capabilities ?: session.capabilities) {

                        val smartUrn = capability.urn.replace("urn:ietf:params:jmap:", "")
                        NavigationDrawerItem(
                            label = { Text(smartUrn) },
                            selected = false,
                            onClick = { session.visibleCapability = capability },
                        )
                    }
                }
            }
        }
    ) {
        if (session.visibleCapability != null)
            CapabilityView(session)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDropdown(session: SessionViewModel) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            value = session.selectedAccount?.name ?: "None",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )

        ExposedDropdownMenu(expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                onClick = { session.selectedAccount = null },
                text = { Text("None") }
            )

            for (account in session.accounts) {
                DropdownMenuItem(
                    onClick = {
                        session.selectedAccount = account
                        expanded = false
                    },
                    text = { Text(account.name) },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}