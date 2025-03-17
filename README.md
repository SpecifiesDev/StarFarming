# StarFarming
A spigot plugin designed to bring elements from popular games HayDay and Stardew to Minecraft servers.

The resource can be found on spigot:
https://www.spigotmc.org/resources/starfarming.106572/

Want to stay up to date with features I'm working on for the current version? Join the remade discord:
https://discord.gg/7eZKjYjBqB



# FAQ

### I don't understand how to use this resource.
There are plans for a dedicated wiki for StarFarming, not sure when that will be. Until then, come to the discord and I will answer any question ASAP.

### Experimental
As of now, I am moving the resource into an experimental state. From this point forward, the resource is considered unstable. I am doing this to make it crystal clear: a lot of changes are going to be coming, and I won't guarantee legacy data that is moved or entirely re-purposed.

### How will updates work?
I will push constant updates to this github, which also push to a webhook inside of the discord.

As of now, I plan to do weekly updates to Spigot on Sunday. This depends on the features implemented, and their complexity. Any changes to this schedule will be gone over in the discord.

### Can we buy your resource?
No.

### I want to become a dev of the project
If you want to add a suggestion, you can join in the conversation in the discord. As of right now, I'm not looking for additional team members.

# What's new in the rewrite?

This is just a top-down view of the major changes. Refer to the discord for detailed information.
```diff
+ Add plant display before harvesting is ready, on harvest attempt.
+ Restructure the internal data and memory cache, in preparation for new features.
+ Tab completion added to all commands that have more than one argument.
+ /setexperience
+ /profile
+ player settings
+ persistently save crops on reload, restart, or server stop event.
+ slight rng value changes for seed drops
+ ? map testing
+ re-designed config file, more intuitive for the end user.
+ move to a maven configuration / setup

- move away from deprecated methods
- remove support for holographicdisplays entirely
- move away from YAML files in favor of a relational database system.
- move away from internal crop runnables (performance issue)
```


