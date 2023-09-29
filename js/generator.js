
// add an event listener to the button with id generate and call the function generateCode
let vars = [
    "alignmentPrice", ]
//     "alignmentProduct", 
//     "sellerPriceSign",
//     "sellerPriceSub", 
//     "sellerPriceComission",
//     "buyerPriceComission",
//     "buyerAdActivityCPM", 
//     "buyerAdActivityPPC",
//     "sellerAdActivityCPM",
//     "sellerAdActivityPPC",
//     "coders",
//     "marketers",
//     "dataScientists"
// ];

document.getElementById("generate").addEventListener("click", () => {
    let varValues = vars.map((varName) => document.getElementById(varName).value)
    generateCode(vars, varValues);
    // popo up
});


function generateCode(varNames, varValues) {
    let generatedCode = "";
    for (let i = 0; i < varNames.length; i++) {
        generatedCode += `target.${varNames[i]}  = ${varValues[i]};\n`;
    }

    eventDecision = 0// document.getElementById("eventDecisions").value;
    generateCode += `target.eventDecisions[turn]=" ${eventDecision} ";\n`;

    let techDecision = getTechDecision();
    techDecision.forEach((decision) => {
        generatedCode += `target.getTechs().get(${decision}).research();\n`;
    });
    alert(generatedCode);

}

function getTechDecision() {
    let techDecision = [];
    for (let i = 0; i < 1; i++) {
        if (document.getElementById("techDecision" + i).checked){
            techDecision.push(i);
        }
    }
    return techDecision;
}