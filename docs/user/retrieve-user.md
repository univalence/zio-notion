# Retrieve a user

To retrieve a user you can call the following function providing the id of the user:

```scala
for {
  user <- Notion.retrieveUser("user-id")
} yield user
```

A user can be either a person or a bot.

For more information, you can check the [notion documentation](https://developers.notion.com/reference/get-user).
