
function isLongText(text){
    return text.length >= 1900;
}

function detectEndOfWord(text, position, look_for_format){
    const slice=text.slice(position);
    if (!look_for_format){
        for (const char of slice){
            position +=1;
            if (char === ' ' || char === ',' || char === '\n'){
                return position;
            }
        }
    }
    else{
        let format_counter = 0;
        for (const char of slice){
            if (char === '`'){
                format_counter += 1;
            }
            position += 1;
            if (format_counter === 3){
                return position;
            }
        }
    }
}

function separateLongTextLocal(text, look_for_format){
    if (isLongText(text)){
        const separator = detectEndOfWord(text, 1900, look_for_format);
        const left= text.slice(0,-(text.length-separator));
        const right = text.slice(separator);
        return [left].concat(separateLongTextLocal(right));
    }
    else{
        return [text];
    }
}

module.exports = {

    separate_long_text(text, look_for_format=false){
        return separateLongTextLocal(text,look_for_format);
    },
    capitalizeFirstLetters(text){
        const arr_text = text.split(/[,.\s]+/);
        for (let i = 0; i < arr_text.length; i++) {
            arr_text[i] = arr_text[i].charAt(0).toUpperCase() + arr_text[i].slice(1);
        }
        return arr_text.join(" ");
    }

};