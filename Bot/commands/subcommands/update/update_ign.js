const {MessageEmbed} = require("discord.js");
const {UPDATE_IGN} = require("../../../configs/embed_thumbnails.json");
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");

module.exports = {
    async execute(interaction) {
        const ign = interaction.options.getString('ign');
        // send to server and edit reply
        const data = {
            discordId: interaction.member.id,
            ign: ign
        }

        axios.patch('http://'+serverIP+':'+serverPort+'/api/player/update/ign', data)
            .then(async function (response) {
                // The request and data is successful.
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Update IGN`)
                    .setColor('GREEN')
                    .setDescription(`You successfully updated your ign to ${ign}.`)
                    .setThumbnail(UPDATE_IGN)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed], ephemeral: true});
            })
            .catch(async function (error) {
                // An error occurred during the request.
                await interaction.reply({content: `${error.response.data.message}`, ephemeral: true});
            })
        
    },
};