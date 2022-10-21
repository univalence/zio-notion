
# `PatchedProperty`
`PatchedProperty` are properties for pages that you intend to create or update

## Supported types
| Property           | Parameters                                                                   |
|--------------------|------------------------------------------------------------------------------|
| PatchedTitle       | title: Seq[RichTextFragment]                                                 |
| PatchedRichText    | richText: Seq[RichTextFragment]                                              |
| PatchedNumber      | number: Double                                                               |
| PatchedCheckbox    | checkbox: Boolean                                                            |
| PatchedSelect      | id: Option[String], name: Option[String]                                     |
| PatchedMultiSelect | multiSelect: List[PatchedSelect]                                             |
| PatchedDate        | start: LocalDate, end: Option[LocalDate]                                     |
| PatchedDateTime    | start: OffsetDateTime, end: Option[OffsetDateTime], timeZone: Option[String] |
| PatchedPeople      | people: Seq[User]                                                            |
| PatchedRelation    | relation: Seq[Id]                                                            |
| PatchedFiles       | files: Seq[Link]                                                             |
| PatchedUrl         | url: String                                                                  |
| PatchedEmail       | email: String                                                                |
| PatchedPhoneNumber | phoneNumber: String                                                          |


## Referenced methods
todo links