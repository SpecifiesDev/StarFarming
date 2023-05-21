# StarFarming
A spigot plugin designed to bring elements from popular games HayDay and Stardew to Minecraft servers.

The resource can be found on spigot:
https://www.spigotmc.org/resources/starfarming.106572/

Want to stay up to date with features I'm working on for the current version? Join the remade discord:
https://discord.gg/B2q9TBNXc2



# FAQ

### I don't understand how to use this resource.
Don't worry, as I finish this rewrite I will extensively document every section of the resource on this github's wiki page, and through the use of youtube videos.

### Why did you discontinue the project?
For months I've been getting questions about why I stopped development of this project. I never intended to "discontinue" it. A month after releasing the snapshot I moved 8 states away and had to handle a lot of life things in those regards. I fully intend to develop this project as an independent gamemode.

### How will updates work?
Each update will correspond to the latest version of Spigot. So, if 1.19.4 is the latest, that will be the version the update is pushed in. I will not ensure backwards compatability, as I fully intend the resources that newer versions of mc offers.

### Can we buy your resource?
I've gotten this question a lot. This resource is popular inside of Chinese MC servers, and I've had quite a few people contact me asking to buy rights to the source. No, I will not be selling the rights to this resource. Obviously I'm open sourcing it, but usage of it will be in accordance to the license I'm sourcing it under. I also do not permit use of my code for monetary gain. Meaning, you can't use the codebase for yourself, and sell a repackaged version of my resource on a marketplace.

### I want to become a dev of the project
When I first started this project, I actually had accepted a dev onto my team. I actually really don't want to do that, as I don't want to take away from my own ideas and vision of the project.

# What's new in the rewrite?

### HolographicDisplays
I've chosen to completely move HD entirely as a dependency. This means that a timer will no longer appear over custom crops. I did this for two reasons. I firstly want the resource to be completely independent, I don't want to use any dependencies. And two, placing holograms over huge fields of crops can and will cause issues for players who may not have the best pc.

### Timers 
I've removed a lot of the timers that the old resource used to use. Due to the way holograms worked, and a minor oversight on my part, every planted crop maintained a separate thread to handle crop growth. This is not the case anymore. When you go to harvest a crop now, the plugin simply compares the difference in time between when you planted the crop, and the time required for maturity. This should greatly optimize memory usage for servers.

### /giveseed
Giveseed now takes a third argument "amount" which allows the executor to specify how much of a seed to give a player. It maxes out at 64, so any number over 64 will auto convert back to a stack.

