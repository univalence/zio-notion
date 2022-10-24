# Database
## Overview
`Database` is the type that represents a Notion database.

You will need this type when trying to manipulate the database structure (when retrieving a database for instance) 

## `Database` parameters

| Parameter      | Type                                            | Notes                                                            |
|----------------|-------------------------------------------------|------------------------------------------------------------------|
| createdTime    | OffsetDateTime                                  | Time at which the page was created                               |
| lastEditedTime | OffsetDateTime                                  | Time at which the page was last edited                           |
| createdBy      | Id                                              | Returns the author's id                                          |
| lastEditedBy   | Id                                              | Returns the ast editor's id                                      |
| id             | String                                          | UUID of the page                                                 |
| title          | Seq[RichTextFragment]                           | Richtext describing the database title                           |
| cover          | Option[Cover]                                   | URI of the page's cover picture file                             |
| icon           | Option[Icon]                                    | Emoji or URI of the page's icon picture file                     |
| parent         | Parent                                          | Parent page or database. Can be used like so: "xxx".asParentPage |
| archived       | Boolean                                         | State of the page archiving                                      |
| properties     | properties:     Map[String, PropertyDefinition] | Property map. PropertyDefinition is described todo link          |
| url            | String                                          | Page's URL                                                       |

## References
