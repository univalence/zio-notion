---
slug: /user
sidebar_position: 3
---

# User

## Retrieve a user

To retrieve a user you can call the following function providing the id of the user:

```scala
val user: ZIO[Notion, NotionError, User] = Notion.retrieveUser("user-id")
```

A user can be either a person or a bot.

For more information, you can check the [notion documentation](https://developers.notion.com/reference/get-user).
