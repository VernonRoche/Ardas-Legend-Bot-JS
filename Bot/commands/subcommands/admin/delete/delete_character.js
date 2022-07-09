const {isMemberStaff} = require("../../../../utils/utilities");
const {MessageEmbed} = require("discord.js");
const {ADMIN} = require("../../../../configs/embed_thumbnails.json");
const {serverIP, serverPort} = require("../../../../configs/config.json");
const axios = require('axios');

module.exports = {
    async execute(interaction) {
        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: true});
            return;
        }
        const discId = interaction.options.getString('discord-id');
        // send to server
        const data = {
            discordId: discId,
        }

        axios.delete('http://'+serverIP+':'+serverPort+'/api/player/delete/rpchar', {data: data})
            .then(async function (response) {
                // The request and data is successful.

                const characterName = response.data.name;

                const replyEmbed = new MessageEmbed()
                    .setTitle(`Delete character`)
                    .setColor('NAVY')
                    .setDescription(`Deleted the character ${characterName} of user with Discord ID ${discId}.`)
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

