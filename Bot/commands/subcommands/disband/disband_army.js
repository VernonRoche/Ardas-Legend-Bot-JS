
module.exports = {
    async execute(interaction) {
        let name=interaction.options.getString('name').toLowerCase();
        //split the above strings into arrays of strings
        //whenever a blank space is encountered

        const arr_name = attacker.split(" ");

        //loop through each element of the array and capitalize the first letter.

        for (let i = 0; i < arr_name.length; i++) {
            arr_name[i] = arr_name[i].charAt(0).toUpperCase() + arr_name[i].slice(1);
        }

        //Join all the elements of the array back into a string
        //using a blankspace as a separator
        name = arr_name.join(" ");
        await interaction.reply(`The army \"${name}\" has been disbanded.`);
    },
};