package com.innovatek.madartask.presentation.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.innovatek.madartask.domain.model.Gender
import com.innovatek.madartask.presentation.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    viewModel: UserViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(Gender.MALE) }
    var genderExpanded by remember { mutableStateOf(false) }

    val ageFocus = remember { FocusRequester() }
    val jobTitleFocus = remember { FocusRequester() }

    val ageInt = age.toIntOrNull()
    val ageError = when {
        age.isNotBlank() && ageInt == null -> "Enter a valid age"
        ageInt != null && ageInt > 100 -> "Age cannot exceed 100 years"
        ageInt != null && ageInt < 1 -> "Age must be at least 1"
        else -> null
    }
    val canSave = name.isNotBlank() && age.isNotBlank() && ageError == null && jobTitle.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add User") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { ageFocus.requestFocus() })
            )

            OutlinedTextField(
                value = age,
                onValueChange = { age = it.filter { c -> c.isDigit() } },
                label = { Text("Age") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(ageFocus),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { jobTitleFocus.requestFocus() }),
                isError = ageError != null,
                supportingText = if (ageError != null) {
                    { Text(ageError, color = MaterialTheme.colorScheme.error) }
                } else null
            )

            OutlinedTextField(
                value = jobTitle,
                onValueChange = { jobTitle = it },
                label = { Text("Job Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(jobTitleFocus),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { genderExpanded = true })
            )

            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = { genderExpanded = it }
            ) {
                OutlinedTextField(
                    value = gender.name.lowercase().replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender") },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false }
                ) {
                    Gender.entries.forEach { g ->
                        DropdownMenuItem(
                            text = { Text(g.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                gender = g
                                genderExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.addUser(
                        name = name.trim(),
                        age = ageInt!!,
                        jobTitle = jobTitle.trim(),
                        gender = gender
                    )
                    onBack()
                },
                enabled = canSave,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 0.dp)
            ) {
                Text("Save", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
