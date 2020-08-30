# FoodleMaps

_FoodleMaps has been developed as project for the Knowledge Engineering course 2019-2020 at Politecnico di Milano._

FoodleMaps is an ontology which can be used to create an application that shows the location of restaurants serving particular dishes.  
For example, an user may look for a particular dish (let’s say a veggie burrito). Then the application should find the restaurants serving such dish, locating them for the user.

The ontology includes a set of classes and properties able to model the domain of restaurants and served dishes. 

![Graphic representation of the ontology](../master/docs/resources/ontology.png)

The ontology is available at: https://alexmv12.github.io/FoodleMaps/  
While a public SPARQL endpoint is available at: https://sparql.alexmv12.xyz/  
You can self-host your own SPARQL endpoint with [Apache Jena](https://hub.docker.com/r/stain/jena-fuseki/).

# Practical examples

Please, refer to the Jupyter Notebook in this repo (FoodleMaps.ipynb).

# Ontology details

The model can be divided into two parts.
The first is the one related to restaurants. The class schema:FoodEstablishment is used to represent every restaurant (or, in general, every place which serves food) in the knowledge base.
Every restaurant, using the schema.org ontology, has one or more schema:Menu and every menu serves some schema:Recipe.

This is the core of the ontology. Every schema:MenuItem represents an entry in the menu, and it has a preparation time (expressed in minutes) and some schema:Characteristic.

We decided to assign one or more schema:Recipe to every schema:MenuItem. This lets us distinguish the various variations of a dish. For example, in the case of items which use pasta, two different recipes can be used to offer both normal pasta or integral one.
Another interesting aspect of the use of recipe is the possibility to make a recipe compliant for a specific schema:RestrictedDiet. For example, a restaurant can offer a sandwich both in the version with meat and in the vegan version (whose recipe will be suitable for the restricted diet “vegan”). 

It may be argued that it would be better to assign MenuItems to each Menu. However, it would be impossible for two restaurants to serve the same dish (e.g. Pasta al pomodoro) using two different recipes.
Assigning directly recipes to menus makes it possible.

Every recipe has one or more Ingredient, which is a sub-class of dbo:Food.

The second part of the ontology is centered on the user. It has a pool of conditions, which will be applied to searches in order to only show the dishes (or restaurant) compliant with such conditions.
For example, an user may request a particular ingredient in the dishes he desires, or, on the contrary, it may request that place with a particular ingredient are not shown.
Moreover, a restriction on the diet and on some dishes’ characteristics can be used.

# Competency questions

- Some example of competency questions are:
- Show me all the restaurants serving dishes with tomatoes.
- Find the nearest restaurant which serves at least a “light” dish.
- Show me a restaurant which does not serve any dish with eggs.
- Show me a restaurant which serves a dish which can be prepared in less than 5 minutes.

# Data linking

To get restaurants informations and their menus, we used RakutenAPI, this provides access to a database of over 350 000 restaurants.
To explain how we got these information and how we exploit them an example is served.
Please note that the example provides 50 restaurants and menus for few of them, because of limited API get requests.

API REQUEST: a json file is retrieved with a get call, then it is formatted to simplify other tools utilization and converted to csv.

YARRRML (): we wrote a set of rules to fit, to the best, our ontology model. these rules are parsed with yarrrml-parser to get RML rules
    yarrrml-parser -i rules.yml -o rules.rml.ttl 

RMLMAPPER (): we used rmlmapper to convert rml rules to RDF triples.
    java -jar /path/to/rmlmapper.jar -m rules.rml.ttl


JENA MODEL ():

rdfmodel is not a perfectly fitting model with ontology;
menu item description is a string that contains every ingredients, instead a list of ingredient it’s necessary

SPARQL GET QUERY : get every item description 

VOCABULARY MATCHING: match descriptions words with a set of foods and dishes name
this is an approximation of how a better vocabulary should work, some words are not food or ingredients.

SPARQL UPDATE QUERY: : every ingredient is added as FoodleMaps:Ingredient and linked with corresponding recipe

### Disclaimer

We used some real-world examples as basic entries for our ontology; we are not affiliated in any way with any entity represented in the ontology, neither any information contained in this ontology has to be considered accurate. We decline any responsability for a wrong use of the information here contained.

