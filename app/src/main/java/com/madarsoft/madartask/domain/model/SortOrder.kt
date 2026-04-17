package com.madarsoft.madartask.domain.model

enum class SortOrder(val label: String) {
    NAME_ASC("Name (A → Z)"),
    NAME_DESC("Name (Z → A)"),
    AGE_ASC("Age (Youngest first)"),
    AGE_DESC("Age (Oldest first)"),
    JOB_TITLE_ASC("Job Title (A → Z)"),
    JOB_TITLE_DESC("Job Title (Z → A)"),
    ID_DESC("Newest first"),
    ID_ASC("Oldest first")
}
