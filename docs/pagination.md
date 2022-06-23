---
slug: /pagination
sidebar_position: 2
---

# Pagination

Some Notion endpoints required a`Pagination` object to work.

You will need to provide this object for the following endpoints:
- Notion.queryDatabase
- Notion.retrieveUsers
- Notion.retrieveBlocks

We need to paginate these endpoints because they can return a huge amount of information. As an example, when you
want to retrieve all the users from Notion you may retrieve thousands of different entities. That's why you may have
to make several queries to retrieve the whole set of users.

A `Pagination` object needs two information:
- How many elements you want to retrieve (Maximum 100 per query)
- When to start the count (Generally this information is given by the previous query, default is None if you want to 
  start from the beginning)

We also provide endpoints that handle the pagination for you:
- Notion.queryAllDatabase
- Notion.retrieveAllUsers
- Notion.retrieveAllBlocks

However, it can be wise to don't use them in some case. For example if you search for a user in particular, you may
want to stop when you find it preventing you to fetch the whole elements.

[Notion pagination documentation](https://developers.notion.com/reference/pagination) for more information.
