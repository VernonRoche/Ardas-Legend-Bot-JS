const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {serverIP, serverPort} = require("../../../configs/config.json");
const {UPDATE} = require('../../../configs/embed_thumbnails.json');
const axios = require("axios");

module.exports = {
    async execute(interaction) {
        const pvp = interaction.options.getBoolean('new-pvp');

        //data sent to server
        const data = {
            discordId: interaction.member.id,
            pvp: pvp
        }

        axios.patch('http://'+serverIP+':'+serverPort+'/api/player/update/rpchar/pvp', data)
            .then(async function() {

                //used to build the description later on
                let description = 'no longer';
                if(pvp)
                    description = 'now';

                //if request successful
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Update RpChar PvP`)
                    .setColor('GREEN')
                    .setDescription(`Your Roleplay Character will ${description} participate in PvP!`)
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