package com.ewoudje.jmapbrowser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ewoudje.jmapbrowser.session.JMapAccountCapability
import com.ewoudje.jmapbrowser.session.JMapCapability
import com.ewoudje.jmapbrowser.session.UnknownJMapCapability
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.collections.component1
import kotlin.collections.component2


enum class CapabilityViewTab(val label: String) {
    OVERVIEW("Overview"),
    INVOKE("Invoke"),
}

@Composable
fun CapabilityView(session: SessionViewModel) {
    val capability = session.visibleCapability ?: return
    var selectedTab by rememberSaveable { mutableStateOf(CapabilityViewTab.OVERVIEW) }

    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        Text(capability.urn, style = MaterialTheme.typography.labelSmall)
        PrimaryTabRow(selectedTabIndex = selectedTab.ordinal) {
            CapabilityViewTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = {
                        selectedTab = tab
                    },
                    text = {
                        Text(
                            text = tab.label,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }


        when (selectedTab) {
            CapabilityViewTab.OVERVIEW -> Column {
                CapabilityOverview(capability)
            }

            CapabilityViewTab.INVOKE -> Text("TODO")
        }
    }
}

@Composable
fun CapabilityOverview(capability: JMapCapability) {
    if (capability is JMapAccountCapability) CapabilityOverview(capability.capability)

    Card(modifier = Modifier.padding(8.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {


            when (capability) {
                is JMapAccountCapability -> {
                    Text("Account Configuration", style = MaterialTheme.typography.headlineSmall)
                    HorizontalDivider()
                    ListJsonEntries(capability.configuration)
                }
                is UnknownJMapCapability -> {
                    Text("Global Configuration", style = MaterialTheme.typography.headlineSmall)
                    HorizontalDivider()
                    ListJsonEntries(capability.configuration)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListJsonEntries(configuration: Map<String, JsonElement>) {
    LazyColumn(state = rememberLazyListState()) {
        items(configuration.entries.toList()) { (key, value) ->
            Row {
                Text(key)
                Spacer(Modifier.fillMaxWidth().weight(1f))
                when (value) {
                    is JsonPrimitive -> Text(value.content)
                    is JsonArray -> TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = { PlainTooltip { Text(value.toString()) } },
                        state = rememberTooltipState()
                    ) { Text("[...]") }
                    is JsonObject -> TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = { PlainTooltip { Text(value.toString()) } },
                        state = rememberTooltipState()
                    ) { Text("{...}") }
                }
            }
        }
    }
}