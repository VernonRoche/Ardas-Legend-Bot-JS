const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {serverIP, serverPort} = require("../../../configs/config.json");
const {UPDATE} = require('../../../configs/embed_thumbnails.json');
const axios = require("axios");

module.exports = {
    async execute(interaction) {
        //name won't get capitalized here so people have more freedom when naming their chars
        const title = interaction.options.getString('new-title');

        //data sent to server
        const data = {
            discordId: interaction.member.id,
            title: title
        }

        axios.patch('http://'+serverIP+':'+serverPort+'/api/player/update/rpchar/title', data)
            .then(async function() {
                //if request successful
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Update RpChar Title`)
                    .setColor('GREEN')
                    .setDescription(`The title of your Roleplay Character has been updated to ${title}!`)
                    .setThumbnail(UPDATE)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                //error occurred
                await interaction.reply({content: `${error.response.data.message}`, ephemeral: true});
            })

    },
};