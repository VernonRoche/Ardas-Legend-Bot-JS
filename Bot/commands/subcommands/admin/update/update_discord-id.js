const {isMemberStaff} = require("../../../../utils/utilities");
const {MessageEmbed} = require("discord.js");
const {ADMIN} = require("../../../../configs/embed_thumbnails.json");
const {serverIP, serverPort} = require("../../../../configs/config.json");
const axios = require("axios");

module.exports = {
    async execute(interaction) {
        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: true});
            return;
        }
        const oldId = interaction.options.getString('old-discord-id');
        const newId = interaction.options.getString('new-discord-id');
        // send to server
        const data = {
            oldDiscordId: oldId,
            newDiscordId: newId
        }

        axios.patch('http://'+serverIP+':'+serverPort+'/api/player/update/discordid', data)
            .then(async function (response) {
                // The request and data is successful.
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Update Discord ID`)
                    .setColor('NAVY')
                    .setDescription(`Updated discord ID of player from ${oldId} to ${newId}.`)
                    .setThumbnail(ADMIN)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function (error) {
                // An error occurred during the request.
                await interaction.reply({content: `${error.response.data.message}`, ephemeral: true});
            })
        
    },
};
