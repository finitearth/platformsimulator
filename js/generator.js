
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

function generateCode(varNames, varValues) {
    let generatedCode = "";
    for (let i = 0; i < varNames.length; i++) {
        generatedCode += `target.${varNames[i]}  = ${varValues[i]};\n`;
    }

    eventDecision = document.getElementById("eventDecision").value;

    // if the event decision is not empty map a to 1, b to 2 and so on
    if (eventDecision != "") {
        eventDecision = eventDecision.charCodeAt(0) - 96;
        generatedCode += `target.eventDecisions[turn]=${eventDecision};\n`;
    } else {
        alert("Note: You did not specify an event decision.");
    }

    // get the tech decisions
    let techDecision = getTechDecision();
    techDecision.forEach((decision) => {
        generatedCode += `target.getTechs().get(${decision}).research();\n`;
    });

    // automatically download the code as txt file
    let element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(generatedCode));
    element.setAttribute('download', "generatedCode.txt");
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);

}

function getTechDecision() {
    let techDecision = [];
    for (let i = 0; i <= 28; i++) {
        if (document.getElementById("techDecision" + i).checked && !document.getElementById("techDecision" + i).disabled){
            techDecision.push(i);
        }
    }
    return techDecision;
}

document.getElementById("generate").addEventListener("click", () => {
    console.log(typeof generateCode);
    let varValues = vars.map((varName) => document.getElementById(varName).value)
    generateCode(vars, varValues);
});


vars.forEach(v => {
    console.log(v)
document.getElementById(v).addEventListener('input', function() {
    var inputValue = this.value;
    // if inputVaulue is a number set inputValue to true
    var isValid = !isNaN(inputValue) && !isNaN(parseFloat(inputValue));
    
    if (isValid) {
      this.classList.remove('is-invalid');
    } else {
      this.classList.add('is-invalid');
    }
  });
});