const {rpAppsChannelID, staffRoles} = require("../configs/config.json");

// Fetch all admins and send them a private message, notifying them there is a new RP app
function notifyStaff(guild, message) {
    const notifiedStaff = [];
    for (const role in staffRoles) {
        const staffMembers = guild.roles.cache.get(role).members.cache;
        for (const member of staffMembers) {
            if (!notifiedStaff.includes(member.id)) {
                member.user.send(`${message}`);
                notifiedStaff.push(member.id);
            }
        }
    }
}

module.exports = {
    name: 'interactionCreate',
    async execute(msg) {
        console.log(`${msg.author} in #${msg.channel.name} triggered an interaction.`);
        if (msg.channel.id === rpAppsChannelID) {
            //console.log(`${msg.author.tag} in #${msg.channel.name} sent a message in roleplay apps.`);
            //if (msg.client.user.id === msg.author.id) return;
            //notifyStaff(msg.guild, `${msg.author} sent an application in roleplay apps.`);
        }
    },
};