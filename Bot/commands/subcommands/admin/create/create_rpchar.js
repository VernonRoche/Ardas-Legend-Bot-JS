const {capitalizeFirstLetters, isMemberStaff} = require("../../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {serverIP, serverPort} = require("../../../../configs/config.json");
const {CREATE} = require('../../../../configs/embed_thumbnails.json');
const axios = require("axios");

module.exports = {
    async execute(interaction) {

        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: true});
            return;
        }

        //name and title won't get capitalized here so people have more freedom when naming their chars
        const name = interaction.options.getString('name');
        const title = interaction.options.getString('title');
        const gear = capitalizeFirstLetters(interaction.options.getString('gear').toLowerCase());
        const pvp = interaction.options.getBoolean('pvp');

        //data sent to server
        const data = {
            discordId: interaction.member.id,
            rpCharName: name,
            title: title,
            gear: gear,
            pvp: pvp
        }

        axios.post('http://'+serverIP+':'+serverPort+'/api/player/create/rpchar', data)
            .then(async function() {
                //if request successful
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Create RpChar`)
                    .setColor('GREEN')
                    .setDescription(`The Roleplay Character ${name} - ${title} has been created!`)
                    .setThumbnail(CREATE)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                //error occurred
                await interaction.reply({content: `${error.response.data.message}`, ephemeral: true});
            })

    },
};