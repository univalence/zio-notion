"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[522],{3905:function(e,t,r){r.d(t,{Zo:function(){return s},kt:function(){return m}});var n=r(7294);function a(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function o(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function i(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?o(Object(r),!0).forEach((function(t){a(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):o(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function p(e,t){if(null==e)return{};var r,n,a=function(e,t){if(null==e)return{};var r,n,a={},o=Object.keys(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||(a[r]=e[r]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(a[r]=e[r])}return a}var l=n.createContext({}),u=function(e){var t=n.useContext(l),r=t;return e&&(r="function"==typeof e?e(t):i(i({},t),e)),r},s=function(e){var t=u(e.components);return n.createElement(l.Provider,{value:t},e.children)},c={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},d=n.forwardRef((function(e,t){var r=e.components,a=e.mdxType,o=e.originalType,l=e.parentName,s=p(e,["components","mdxType","originalType","parentName"]),d=u(r),m=a,f=d["".concat(l,".").concat(m)]||d[m]||c[m]||o;return r?n.createElement(f,i(i({ref:t},s),{},{components:r})):n.createElement(f,i({ref:t},s))}));function m(e,t){var r=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var o=r.length,i=new Array(o);i[0]=d;var p={};for(var l in t)hasOwnProperty.call(t,l)&&(p[l]=t[l]);p.originalType=e,p.mdxType="string"==typeof e?e:a,i[1]=p;for(var u=2;u<o;u++)i[u]=r[u];return n.createElement.apply(null,i)}return n.createElement.apply(null,r)}d.displayName="MDXCreateElement"},3326:function(e,t,r){r.r(t),r.d(t,{assets:function(){return s},contentTitle:function(){return l},default:function(){return m},frontMatter:function(){return p},metadata:function(){return u},toc:function(){return c}});var n=r(7462),a=r(3366),o=(r(7294),r(3905)),i=["components"],p={},l="Retrieve a page",u={unversionedId:"page/retrieve-page",id:"page/retrieve-page",title:"Retrieve a page",description:"To retrieve a page you can call the following function providing the id of the page:",source:"@site/../docs/page/retrieve-page.md",sourceDirName:"page",slug:"/page/retrieve-page",permalink:"/zio-notion/page/retrieve-page",draft:!1,editUrl:"https://github.com/univalence/zio-notion/edit/master/docs/../docs/page/retrieve-page.md",tags:[],version:"current",frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Introduction",permalink:"/zio-notion/"},next:{title:"Update a page",permalink:"/zio-notion/page/update-page"}},s={},c=[{value:"Deal with properties",id:"deal-with-properties",level:2}],d={toc:c};function m(e){var t=e.components,r=(0,a.Z)(e,i);return(0,o.kt)("wrapper",(0,n.Z)({},d,r,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"retrieve-a-page"},"Retrieve a page"),(0,o.kt)("p",null,"To retrieve a page you can call the following function providing the id of the page:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},'for {\n  page <- Notion.retrievePage("page-id")\n} yield page\n')),(0,o.kt)("p",null,"This function does not retrieve the content of a page but only the metadata. We tend to follow the notion api, and they\ndon't provide this information when you retrieve a page. Instead, you have to call\n",(0,o.kt)("a",{parentName:"p",href:"https://developers.notion.com/reference/get-block-children"},"this endpoint")," which is not implement in ZIO Notion yet."),(0,o.kt)("p",null,"For more information, you can check the ",(0,o.kt)("a",{parentName:"p",href:"https://developers.notion.com/reference/retrieve-a-database"},"notion documentation"),"."),(0,o.kt)("h2",{id:"deal-with-properties"},"Deal with properties"),(0,o.kt)("p",null,"It can be troublesome to deal with page's properties. Indeed, the properties is a map composed by properties that can\ntheoretically be of any kind."),(0,o.kt)("p",null,"As an example, if you want to retrieve a number you will first have to : "),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"ensure that the property exist in the database"),(0,o.kt)("li",{parentName:"ul"},"ensure that the property is indeed a number property"),(0,o.kt)("li",{parentName:"ul"},"ensure that the property is fulfilled with data")),(0,o.kt)("p",null,"You will easily have to write something like this:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},'val maybeProperty: Option[Property] = page.properties.get("name")\n\nmaybeProperty.collect{\n  case number: Property.Number => number.number match {\n    case Some(value) => // we can finally do something with the value\n    case None => // the value exists in the database but the row has no data in it\n  }\n}\n')),(0,o.kt)("p",null,"That's why you can use the page function ",(0,o.kt)("inlineCode",{parentName:"p"},"propertiesAs[A]")," to convert your properties into a defined case class.\nUnder the hood, it uses Magnolia to automatically derive the case class."),(0,o.kt)("p",null,"If we take the same example, you can now write something like this:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"case class PropertiesRepresentation(number: Double)\n\nval propertiesOrError = page.propertiesAs[PropertiesRepresentation]\n\npropertiesOrError.map(_.number)\n")),(0,o.kt)("p",null,"It will return a ",(0,o.kt)("inlineCode",{parentName:"p"},"Validation[ParsingError, A]"),", this data structure is provided by\n",(0,o.kt)("a",{parentName:"p",href:"https://zio.github.io/zio-prelude/docs/functionaldatatypes/validation"},"zio-prelude"),"."),(0,o.kt)("p",null,"This way of dealing with properties has several interesting features:"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"You can deal with optional value"),(0,o.kt)("li",{parentName:"ul"},"You can deal with list of values (Multiselect, People, etc.)"),(0,o.kt)("li",{parentName:"ul"},"You can create encoder for your own enumeration (Select)")),(0,o.kt)("p",null,"I advise you to look at the example\n",(0,o.kt)("a",{parentName:"p",href:"https://github.com/univalence/zio-notion/tree/master/examples/retrieve-page"},"retrieve-page")," for more information."))}m.isMDXComponent=!0}}]);