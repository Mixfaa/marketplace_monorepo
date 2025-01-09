idc about frontend, but i did it with svelte

Backend contains 3 services
- main marketplace service (rest api~), manages products, wishlist, users, categories, and maybe smth else, i don`t remember
- indexer - manages required properties of categories, and smth with products properties. Have rest methods to access indexes of categories, it talks to main service via RabbitMQ MessageBroker
- gateway router
  
