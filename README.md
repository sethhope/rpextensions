
****What is RP Extensions?****  
RP Extensions is a simple plugin dedicated to adding simple, but needed, Role Playing elements to any spigot server. This plugin is specially designed for RP servers that need that extra layer of realism.  
  
****What's in RP Extensions?****  

-   A Gold Nugget based money storage system for those physical economy servers complete with ATM machines!
-   A thirst system so your users can finally have a use for those water bottles (along with a /quench command for admin use)
-   A basic chair system! Use blocks of your choice as a chair!
-   Rest requirements. Make sure you get a good nights sleep!
-   Permissions enabled! Use with a permission plugin of your choice!  
    
-   Essentials integrated!
-   Vault integrated!
-   Fully configurable for selecting the features you wish to have active

**Other features that are planned are:**  

-   Job system so your users have something to do
-   Multi-verse integration for World Specific player data  
    
-   Anything that you suggest that we like!

****Commands and Permissions****  
**Permissions:**  

-   rpext.* : All permission for everything (op only by default)
-   rpext.read : allow use of /stats. Everyone by default.
-   rpext.checkstats : allows users to check eachother's stats
-   rpext.statmonitor: allows users to spawn in a stat monitor item
-   rpext.setstats: allows users to set the stats of others  
    
-   rpext.gstore : allow users to store gold. Only by permission.
-   rpext.gtake : allow users to take gold. Only by permission.
-   rpext.gtransfer : allows users to transfer gold. Only by permission.
-   rpext.ggive : allows users to give gold. Only by permission
-   rpext.gset : allows users to set others accounts. Only by permission
-   rpext.quench : allow users to quench their own thirst. Only by permission.
-   rpext.quench.others: allows users to quench other's thirst  
    
-   rpext.createatm : allow users to create atm machines at will. Create a sign with [rpAtm] as the first line to create a machine.
-   rpext.sleep : allows users to satiate their sleep requirements  
    
-   rpext.sleep.others: allows users to satiate the sleep requirement of others.  
    
-   rpext.convertEcon : allows users to convert the economy system from RP Extensions native to Vault (Useful for migrating in the future)

**Commands:**  

-   /stats: Displays user stats
-   /checkstats [player]: Displays stats of the specified player.
-   /statmonitor: Spawns a stat monitor item for easy thirst and sleep tracking  
    
-   /setstats [player] [thirst] [sleep]: Sets the thirst and sleep values of a specified player  
    
-   /gstore [amount] : stores a specified amount of gold nuggets at an ATM.
-   /gtake [amount] : takes a specified amount of gold nuggets at an ATM.
-   /gtransfer [player] [amount]: transfers the specified amount of gold nuggets to [player]'s account
-   /gset [player] [amount]: sets the [player]'s bank account to the specified amount
-   /quench ([player]): quenches the thirst of the command sender.
-   /sleep ([player]): Simulates sleeping on the user (resets sleepiness to 20)
-   /convertEcon : converts the economy data of a server to Vault
