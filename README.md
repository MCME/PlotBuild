# PlotBuild

#### Authors: Ivan1pl, Eriol_Eandur

*PlotBuild* is a bukkit plugin that generates plots where players can build in.

# Usage

Full list of commands you can execute.

## As normal user
#### /plot info

When inside a plot displays a link to the forum post with build instructions. Shows also a list of staff members of the plotbuild.

#### /plot list

Lists all running Plotbuilds along with number of free plots.

#### /plot claim

When standing inside an unclaimed plot, claims the plot. This gives build permissions inside the plot and displays the player name on a sign at the plot. Can not be used when the plotbuild is private.

#### /plot invite &lt;player&gt;

When inside a plot and user is builder of that plot, adds __player__ to the building team of the plot. Both players then can build inside that plot. Sign is updated with all player names. Maximum of players in a building team is 8. After that every player of the building team can invite other players.

#### /plot leave

When standing inside a plot and plot is owned by multiple players, leaves the building team, which would then allow the leaving player to claim another plot. If plot is owned by only one player, command fails and suggests to use /plot unclaim instead.

#### /plot finish

When inside a plot and user is builder of the plot, the plot is marked as being finished / ready for check and notifies staffs of the plotbuild. It is required to mark a plot as finished before it is possible to claim another plot.

#### /plot unclaim

When inside a plot and user is the only builder of the plot, unclaims and resets plot. For resetting see notes on __/plot clear__.

#### /plot help [command]

Shows a usage description for [command]. If [command] is not specified a list of short descriptions for all plotbuild commands are shown.

## As staff
#### /plot create &lt;name&gt; [bordertype] [height] [-p] [-3D]

Defines a new plotbuild, __name__ is e.g. the name of the village that is to be built. __bordertype__ is either ground, float or none and defines whether the plot border (which is marked with wool) should be on the ground (default), floating in air, at y-value __height__, or have no visible borders at all. The optional flag __-p__ makes the plotbuild **p**rivate, which makes it impossible to claim and invite players to a plot. Instead staff can assign players to a plot. This can be used for plotbuilds that need signing up on the forums before for example. Without the optional __-3D__ flag the plots  are rectangular boxes with full height of the map. When the __-3D__ flag is used for every Plot a bottom and top is stored. The player who issued this command becomes staff of the new  plotbuild which will make him receive messages about events in the plotbuild.

#### /plot new [name]

Creates a new plot to the current plotbuild, respectively to plotbuild __name__ if specified. The plot is always rectangular. To select the area of the plot you have to right/left click two blocks with feather. These blocks are used as opposite corners of the plot. In plotbuilds which were created with the -3D flag the corner block are used to define bottom and top of the plot. In other plotbuilds the plots span the full height of the map.

#### /plot setinfo &lt;URL&gt; [name]

Adds the __URL__ of the forum post with build instructions to the current plotbuild respectively the plotbuild __name__ (optional to set).

#### /plot current &lt;name&gt;

Sets the plotbuild __name__ as current plotbuild. That way it is not necessary to put the name of the plotbuild to work on in every command. The command /plot create sets the current plotbuild too. Current plotbuild is stored for every staff separately.

#### /plot addstaff &lt;player&gt; [name]

For public projects that want to feature a plotbuild. This command gives the (non-staff) project leader __player__ access to the staff commands for the current respectively plotbuild __name__. Staff members of a plotbuild receive messages about events in the plotbuild. Thus MCME staff who manages a plotbuild may want to use this command too. It is possible to add yourself to staff.

#### /plot removestaff &lt;player&gt; [name]

This command removes the staff member __player__ from the plotbuild staff. May be useful when an other staff overtakes a plotbuild and the first staff doesn’t want to get the event messages any more. You can remove yourself.

#### /plot assign &lt;player&gt;

When standing in a plot assigns __player__ to the plot, up to 8 players can be assigned to one plot. Else it is similar to /plot claim just that it’s issued by staff. Can be used on both public and private plotbuilds.

#### /plot accept

When inside a claimed plot, accepts the build inside the plot, deletes the plot (build perms for builders and borders) and notifies the builders. When the last plot of a plotbuild is accepted the staff who issued the command is asked if the plotbuild should be ended as described at __/plot end__.

#### /plot refuse

When inside a claimed plot can be used if there are still changes needed. Changes can be marked directly on the build. Notifies builders.

#### /plot clear [-u]

When inside a plot resets the plot to the initial state. If the plot was claimed, notifies builders.
__-u__ is an optional flag, if used it does also unclaim the plot, default just clears the plot but does not unclaim it.

#### /plot delete [-k]

When inside a plot removes the plot (borders and build perms). Plots does not necessarily need to be claimed. By default also the changes made inside the plot are rolled back to the initial state, the flag __-k__ can be used to **k**eep the changes. If plot was claimed, notifies builders. When the last open plot of a plotbuild is deleted the staff who issued the command is asked if the plotbuild should be ended as described at __/plot end__.

#### /plot ban &lt;player&gt; [name]

Restricts __player__ from claiming any further plots in the plotbuild __name__. Also prevents others to invite or assign him. If __name__ is not specified, current plotbuild is used. Unclaims and clears all plots where __player__ is the only builder and that have not been accepted. Notifies builders.

#### /plot unban &lt;player&gt; [name]

Unbans __player__ from current plotbuild, resp. from plotbuild __name__.

#### /plot history [name] [#]

Displays all actions that happened in current plotbuild respectively plotbuild __name__, e.g. "date|time:&lt;player&gt; unclaimed plot #3", "date|time:&lt;staff&gt; accepted plot #1". If there are more than 10 actions the page of history to be shown can be chosen by the number #.

#### /plot lock [name]

Locks the plotbuild __name__, or with no further argument given, all plotbuilds. This prevents non-staff to build on plots, claim new plots and invite players to plots. This can be used during jobs to get the people working on the plotbuilds to help on the job.

#### /plot unlock [name]

Reverts the effects of above command.

#### /plot sign

When inside a plot places the wool blocks of the border and the plot signs again.

#### /plot end &lt;name&gt; [-k]

Ends plotbuild __name__. With __-k__ keeps current plot state. Otherwise all plots that that have not yet been accepted are reset to their initial states. This command removes all saved plot and history data.
