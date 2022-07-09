const {rpAppsChannelID, upvoteEmoji, downvoteEmoji, staffRoles, staffMajorityCount} = require("../configs/config.json");

function countRPAppReactions(reaction) {
    if (reaction.message.channel.id === rpAppsChannelID) {
        // Now the message has been cached and is fully available
        console.log(`${reaction.message.author}'s message "${reaction.message.content}" gained a reaction!`);
        // The reaction is now also fully available and the properties will be reflected accurately:
        console.log(`${reaction.count} user(s) have given the same reaction to this message!`);

        if ([upvoteEmoji, downvoteEmoji].includes(reaction.emoji.name)) {
            // If the reaction is an upvote or downvote, check if it's by staff
            for (const role of staffRoles) {
                if (reaction.message.member.roles.cache.has(role)) {
                    // If the user is staff, check reaction count
                    const upvotes = reaction.message.reactions.cache.get(upvoteEmoji).count;
                    const downvotes = reaction.message.reactions.cache.get(downvoteEmoji).count;

                    if (upvotes + downvotes >= staffMajorityCount) {
                        // App has the necessary votes. Send result to player and if it's approved send to server
                        if (upvotes > downvotes) {
                            console.log("Approved!");
                        } else {
                            console.log("Denied!");
                        }
                    }
                    return;
                }
            }
        }

    }
}


module.exports = {
    name: 'messageReactionAdd',
    async execute(reaction, user) {
        // Check if it's the bot which added the reaction
        if (reaction.me === true) return;
        // When a reaction is received, check if the structure is partial
        if (reaction.partial) {
            // If the message this reaction belongs to was removed, the fetching might result in an API error which should be handled
            try {
                await reaction.fetch();
            } catch (error) {
                console.error('Something went wrong when fetching the message:', error);
                // Return as `reaction.message.author` may be undefined/null
                return;
            }
        }
        countRPAppReactions(reaction);

    },
};