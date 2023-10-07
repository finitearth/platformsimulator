
// add an event listener to the button with id generate and call the function generateCode
let vars = [
    "alignmentPrice",
    "alignmentProduct",
    "coders",
    "dataScientists",
    "marketers",
    "mods",
    "buyerPriceSign",
    "buyerPriceSub",
    "buyerPriceComission",
    "buyerAdActivityCPM",
    "buyerAdActivityPPC",
    "buyerFreemium",
    "sellerPriceSign",
    "sellerPriceSub",
    "sellerPriceComission",
    "sellerAdActivityCPM",
    "sellerAdActivityPPC",
    "sellerFreemium"
];

let fieldsValid = true;

function generateCode(varNames, varValues) {
    // check if all fields are valid
    if (!fieldsValid) {
        alert("Note: You did not specify all fields correctly.");
        throw Error("Error 001");
    }
    let generatedCode = "";
    for (let i = 0; i < varNames.length; i++) {
        if (varNames[i] == "buyerPriceComission") {
            generatedCode += `target.buyerPriceComission  = ${varValues[i].split(",")[0]};\n`;
            generatedCode += `target.buyerPriceAction  = ${varValues[i].split(",")[1]};\n`;

        } else if (varNames[i] == "sellerPriceComission") {
            generatedCode += `target.sellerPriceComission  = ${varValues[i].split(",")[0]};\n`;
            generatedCode += `target.sellerPriceAction  = ${varValues[i].split(",")[1]};\n`;

        }else {
        generatedCode += `target.${varNames[i]}  = ${varValues[i]};\n`;
        }
    }

    eventDecision = document.getElementById("eventDecision").value;

    // if the event decision is not empty map a to 1, b to 2 and so on
    // match if lower case letter is between a and z and only one char
    if (eventDecision.match(/[a-z]{1}/i)) {
        eventDecision = eventDecision.charCodeAt(0) - 96;
        generatedCode += `target.eventDecisions[turn]=${eventDecision};\n`;
    } else {
        alert("Note: You did not specify the event decision correctly.");
        throw Error("Error 002");
    }

    // get the tech decisions
    let techDecision = getTechDecision();
    techDecision.forEach((decision) => {
        generatedCode += `target.getTechs().get(${decision}).research();\n`;
    });

    // automatically download the code as txt file
    let element = document.createElement('a');
    // element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(generatedCode));
    // element.setAttribute('download', "generatedCode.txt");
    element.setAttribute('href', 'mailto:blochinger@blochy.bloch?subject=Entscheidungen&body=' + encodeURIComponent(generatedCode));
    element.setAttribute('target', "_blank");
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);

}

function getTechDecision() {
    let techDecision = [];
    for (let i = 0; i <= 28; i++) {
        if (document.getElementById(`tech${i}-checkbox`).checked && !document.getElementById(`tech${i}-checkbox`).disabled) {
            techDecision.push(i);
        }
    }
    return techDecision;
}

document.getElementById("generate").addEventListener("click", () => {
    let varValues = vars.map((varName) => document.getElementById(varName).value)
    generateCode(vars, varValues);
});


vars.forEach(v => {
    // check if v ends with Freemium
    if (v.endsWith("Freemium")) {

        document.getElementById(v).addEventListener('input', function () {
            var inputValue = this.value;
            // if inputVaulue is true or false set inputValue to true
            var isValid = inputValue == "true" || inputValue == "false";
            fieldsValid = isValid;

            if (isValid) {
                this.classList.remove('is-invalid');
            } else {
                this.classList.add('is-invalid');
            }


        });
    } else if (v.endsWith("Comission")){

        document.getElementById(v).addEventListener('input', function () {
            var inputValue = this.value;
            // if inputVaulue consists of two comma seperated double numbers set inputValue to true
            var isValid = inputValue.match(/^[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?$/);
            fieldsValid = isValid;

            if (isValid) {
                this.classList.remove('is-invalid');
            } else {
                this.classList.add('is-invalid');
            }
        });

    } else {

        document.getElementById(v).addEventListener('input', function () {
            var inputValue = this.value;
            // if inputVaulue is a number set inputValue to true
            var isValid = !isNaN(inputValue) && !isNaN(parseFloat(inputValue));
            fieldsValid = isValid;

            if (isValid) {
                this.classList.remove('is-invalid');
            } else {
                this.classList.add('is-invalid');
            }
        });
    }
});

let allObjects = document.querySelectorAll('[id^="tech"]');
allObjects.forEach((obj) => {
    let objTitle = obj.id.split("-")[0];
    let hoverObjects = document.querySelectorAll('[id^="' + objTitle + '"]');
    obj.addEventListener("mouseover", () => {
        hoverObjects.forEach((hoverObject) => {
            hoverObject.classList.add("hovered-stage");
        });
    });
    obj.addEventListener("mouseout", () => {
        hoverObjects.forEach((hoverObject) => {
            hoverObject.classList.remove("hovered-stage");
        });
    });
});
