# Retrieve a page

To retrieve a page you can call the following function providing the id of the page:

```scala
for {
  page <- Notion.retrievePage("page-id")
} yield page
```

This function does not retrieve the content of a page but only its metadata. We tend to follow the notion api, and they
don't provide this information when you retrieve a page. Instead, you have to call
[this endpoint](https://developers.notion.com/reference/get-block-children) which is not implement in ZIO Notion yet.

For more information, you can check the [notion documentation](https://developers.notion.com/reference/retrieve-a-database).

## Deal with properties

It can be troublesome to deal with page's properties. Indeed, the properties is a map composed by properties that can 
theoretically be of any kind.

As an example, if you want to retrieve a number you will first have to : 
- ensure that the property exist in the database
- ensure that the property is indeed a number property
- ensure that the property is fulfilled with data

You will easily have to write something like this:

```scala
val maybeProperty: Option[Property] = page.properties.get("name")

maybeProperty.collect{
  case number: Property.Number => number.number match {
    case Some(value) => // we can finally do something with the value
    case None => // the value exists in the database but the row has no data in it
  }
}
```

That's why you can use the page function `propertiesAs[A]` to convert your properties into a defined case class.
Under the hood, it uses Magnolia to automatically derive the case class.

If we take the same example, you can now write something like this:

```scala
case class PropertiesRepresentation(number: Double)

val propertiesOrError = page.propertiesAs[PropertiesRepresentation]

propertiesOrError.map(_.number)
```

It will return a `Validation[ParsingError, A]`, this data structure is provided by 
[zio-prelude](https://zio.github.io/zio-prelude/docs/functionaldatatypes/validation).

This way of dealing with properties has several interesting features:
- You can deal with optional value
- You can deal with list of values (Multiselect, People, etc.)
- You can create encoder for your own enumeration (Select)

I advise you to look at the example 
[retrieve-page](https://github.com/univalence/zio-notion/tree/master/examples/retrieve-page) for more information.