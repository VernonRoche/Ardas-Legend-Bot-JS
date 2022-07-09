const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {serverIP, serverPort} = require("../../../configs/config.json");
const {UPDATE} = require('../../../configs/embed_thumbnails.json');
const axios = require("axios");

module.exports = {
    async execute(interaction) {
        //name won't get capitalized here so people have more freedom when naming their chars
        const gear = interaction.options.getString('new-gear');

        //data sent to server
        const data = {
            discordId: interaction.member.id,
            gear: gear
        }

        axios.patch('http://'+serverIP+':'+serverPort+'/api/player/update/rpchar/gear', data)
            .then(async function() {
                //if request successful
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Update RpChar Gear`)
                    .setColor('GREEN')
                    .setDescription(`The gear of your Roleplay Character has been updated to ${gear}!`)
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