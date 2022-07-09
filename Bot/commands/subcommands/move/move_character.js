const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {MOVE_CHARACTER} = require('../../../configs/embed_thumbnails.json');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require('axios');

module.exports = {
    async execute(interaction) {
        const destination = interaction.options.getString('end-region');

        const data = {
            discordId: interaction.member.id,
            toRegion: destination
        }

        axios.post('http://'+serverIP+':'+serverPort+'/api/movement/move-char', data)
            .then(async function(response) {

                const name = response.data.player.rpChar.name;
                const start = response.data.path.path[0];
                const path = response.data.path.path;
                const cost = response.data.path.cost;

                const startMsg = `${name} started his movement to region ${destination}.`;
                const pathMsg = `The route will be: ${path.join(" -> ")}.`;
                const costMsg = `The movement will take ${cost} day(s).`
                const description = startMsg + '\n' + pathMsg + '\n' + costMsg;

                const replyEmbed = new MessageEmbed()
                    .setTitle(`Move character`)
                    .setColor('YELLOW')
                    .setDescription(description)
                    .setThumbnail(MOVE_CHARACTER)
                    .setTimestamp()

                await interaction.deferReply();
                await interaction.editReply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                //error occurred
                await interaction.reply({content: `${error.response.data.message}`, ephemeral: true});
            })


    },
};