# `Page`
## Overview
`Page` is the type that represents a Notion page.

You will need this type when handling database query results, retrieving a page or as a result of a page you just created or updated.

## `Page` parameters
    
| Parameter       | Type                  | Notes                                                                |
|-----------------|-----------------------|----------------------------------------------------------------------|
| createdTime     | OffsetDateTime        | Time at which the page was created                                   |
| lastEditedTime  | OffsetDateTime        | Time at which the page was last edited                               |
| createdBy       | Id                    | Returns the author's id                                              |
| lastEditedBy    | Id                    | Returns the ast editor's id                                          |
| id              | String                | UUID of the page                                                     |
| cover           | Option[Cover]         | URI of the page's cover picture file                                 |
| icon            | Option[Icon]          | Emoji or URI of the page's icon picture file                         |
| parent          | Parent                | Parent page or database. Can be used like so: "xxx".asParentDatabase |
| archived        | Boolean               | State of the page archiving                                          |
| properties      | Map[String, Property] | Property map. Property is described in todo link                     |
| url             | String                | Page's URL                                                           |

## Referenced methods
TODO
## Recap
todo
## Full example
todo