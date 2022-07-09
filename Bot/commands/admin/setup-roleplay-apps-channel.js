// This command is used to setup a channel which accepts roleplay applications

const {SlashCommandBuilder} = require("@discordjs/builders");
const fs = require('fs');
const filename = '../../configs/config.json';
const config = require(filename);
const {MessageEmbed} = require('discord.js');
const {ADMIN} = require('../../configs/embed_thumbnails.json');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('setup-roleplay-apps-channel')
        .setDescription('Used by admins to specify which channel will accept RP apps.')
        .addStringOption(option =>
            option.setName('channel-id')
                .setDescription('The ID of the channel.')
                .setRequired(true)),
    async execute(interaction) {
        config.rpAppsChannelID = interaction.options.getString('channel-id');
        // DO NOT CHANGE THIS PATH, ITS STATIC AND MOST IMPORTANTLY DO NOT MOVE THE LOCATION OF THE CONFIG FILE
        fs.writeFile('./Bot/configs/config.json', JSON.stringify(config, null, 2), function writeJSON(err) {
            if (err) return console.log(err);
            console.log(JSON.stringify(config));
        });
        const replyEmbed = new MessageEmbed()
            .setTitle(`Roleplay applications channel setup`)
            .setColor('NAVY')
            .setDescription(`Successfully set the default roleplay applications channel.`)
            .setThumbnail(ADMIN)
            .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};