Object.prototype.apply2 = function() {
    if (typeof this == "function") {
        return this.apply(this, arguments)
    }
    else return this.valueOf()
}

var nodes = [
    {id: "rtp-debtor-payment-service",           shape: {from: {col: 1, row: 1}, to: {col: 1, row: 1}}, name: "ODFI Payment Service"},
    {id: "rtp-debtor-send-payment",              shape: {from: {col: 2, row: 1}, to: {col: 2, row: 1}}, name: "ODFI Send Payment"},
    {id: "rtp-mock",                             shape: {from: {col: 3, row: 1}, to: {col: 3, row: 3}}, name: "Fed/TCH"},
    {id: "rtp-creditor-receive-payment",         shape: {from: {col: 4, row: 1}, to: {col: 4, row: 1}}, name: "RDFI Receive Payment"},
    {id: "rtp-creditor-payment-confirmation",    shape: {from: {col: 4, row: 3}, to: {col: 4, row: 3}}, name: "RDFI Payment Confirmation"},
    {id: "rtp-creditor-payment-acknowledgement", shape: {from: {col: 4, row: 2}, to: {col: 4, row: 2}}, name: "RDFI Payment Acknowledgement"},
    {id: "rtp-creditor-complete-payment",        shape: {from: {col: 5, row: 3}, to: {col: 5, row: 3}}, name: "RDFI Complete Payment"},
    {id: "rtp-debtor-payment-confirmation",      shape: {from: {col: 2, row: 3}, to: {col: 2, row: 3}}, name: "ODFI Payment Confirmation"},
    {id: "rtp-debtor-complete-payment",          shape: {from: {col: 1, row: 3}, to: {col: 1, row: 3}}, name: "ODFI Complete Payment"},
    {id: "rtp-creditor-core-banking",            shape: {from: {col: 5, row: 4}, to: {col: 5, row: 4}}, name: "RDFI Core Banking/Ledger"},
    {id: "rtp-debtor-core-banking",              shape: {from: {col: 1, row: 4}, to: {col: 1, row: 4}}, name: "ODFI Core Banking/Ledger"}
]

var edges = [
    {from: "rtp-debtor-payment-service",           to: "rtp-debtor-send-payment"},
    {from: "rtp-debtor-payment-service",           to: "rtp-debtor-complete-payment"},
    {from: "rtp-debtor-send-payment",              to: "rtp-mock", toPart: {col: 1, row: 1}},
    {from: "rtp-mock", fromPart: {col: 1, row: 1}, to: "rtp-creditor-receive-payment"},
    {from: "rtp-mock", fromPart: {col: 1, row: 3}, to: "rtp-creditor-payment-confirmation"},
    {from: "rtp-mock", fromPart: {col: 1, row: 3}, to: "rtp-debtor-payment-confirmation"},
    {from: "rtp-creditor-receive-payment",         to: "rtp-creditor-payment-acknowledgement"},
    {from: "rtp-creditor-receive-payment",         to: "rtp-creditor-complete-payment"},
    {from: "rtp-creditor-payment-confirmation",    to: "rtp-creditor-complete-payment"},
    {from: "rtp-creditor-payment-acknowledgement", to: "rtp-mock", toPart: {col: 1, row: 2}},
    {from: "rtp-creditor-complete-payment",        to: "rtp-creditor-core-banking"},
    {from: "rtp-debtor-payment-confirmation",      to: "rtp-debtor-complete-payment"},
    {from: "rtp-debtor-complete-payment",          to: "rtp-debtor-core-banking"},
]

let width = 150;
let height = 100;
let hSpacing = 100;
let vSpacing = 50;
let svgPadding = 5;
let viz = d3.select("#viz")
let diagram = viz.select("#diagram")

let gNode = diagram.append("g")
    .attr("id", "nodes")
    .selectAll("g")
    .data(nodes)
    .enter().append('g')
    .attr("class", "nodeGroup")

gNode.append("rect")
    .attr("height", node => (node.shape.to.row - node.shape.from.row + 1) * height + (node.shape.to.row - node.shape.from.row) * vSpacing)
    .attr("width", node => (node.shape.to.col - node.shape.from.col + 1) * width + (node.shape.to.col - node.shape.from.col) * hSpacing)
    .attr("rx", 20)
    .attr("ry", 20)
    .attr("x", node => (node.shape.from.col - 1) * hSpacing + (node.shape.from.col - 1) * width + svgPadding)
    .attr("y", node => (node.shape.from.row - 1) * vSpacing + (node.shape.from.row - 1) * height + svgPadding)
    .attr("data-shape", node => JSON.stringify(node.shape))
    .attr("id", node => node.id)
    .attr("class", "service")

gNode.append("text")
    .text(node => node.name)
    .attr("x", node => (node.shape.from.col - 1) * hSpacing + (node.shape.from.col - 1) * width + svgPadding)
    .attr("y", node => (node.shape.from.row - 1) * vSpacing + (node.shape.from.row - 1) * height + svgPadding)
    .attr('font-size', 15)
    .call(fit, 10)

let gEdges = diagram.append("g")
    .attr("id", "edges")
    .selectAll("line")
    .data(edges)
    .enter()

gEdges.append("line")
    .attr("x1", edge => calculateEdgeCoordinates(edge).x1)
    .attr("x2", edge => calculateEdgeCoordinates(edge).x2)
    .attr("y1", edge => calculateEdgeCoordinates(edge).y1)
    .attr("y2", edge => calculateEdgeCoordinates(edge).y2)
    .attr("marker-end", "url(#arrowHead)")

d3.select("#dollarSign")
    .attr("data-current-location", "rtp-debtor-payment-service")

// messageAt("SOMEMESSAGEID", "rtp-debtor-payment-service")


function messageAt(messageId, nodeId, nodePart) {
    let node = d3.select(`#${nodeId}`)
    let messages = d3.select('#messages')

    let x = calculateXCoordinateOnRect(nodeId, nodePart) - 30
    let y = calculateYCoordnateOnRect(nodeId, nodePart) - 13

    messages.selectAll(`#${messageId}`)
        .data([messageId])
        .join("use")
        .attr("xlink:href", "#dollarSignDef")
        .attr("id", messageId)
        .attr("data-current-location", nodeId)
        .attr("data-current-location-part", JSON.stringify(nodePart))
        .attr("transform", `translate(${x}, ${y})`)
}


function calculateEdgeCoordinates(edge) {
    let x1 = calculateXCoordinateOnRect(edge.from, edge.fromPart)
    let x2 = calculateXCoordinateOnRect(edge.to, edge.toPart)
    let y1 = calculateYCoordnateOnRect(edge.from, edge.fromPart)
    let y2 = calculateYCoordnateOnRect(edge.to, edge.toPart)

    let xDirection = (x2 - x1) / Math.abs(x2 - x1) || 0
    x1 += xDirection * width / 2 // If xDirection is negative (because the line is going backwards) x1 will be moved to the left by half of the width of the rect.
    x2 -= xDirection * width / 2 // Conversely, if the xDirection is negative, x2 will be moved to the right by half of the width of the rect.

    let yDirection = (y2 - y1) / Math.abs(y2 - y1) || 0
    y1 += yDirection * height / 2
    y2 -= yDirection * height / 2

    return {x1, x2, y1, y2}
}


function calculateYCoordnateOnRect(id, part){
    let y
    let rect = d3.select(`#${id}`)
    if (part)
        y = Number(rect.attr("y")) + height / 2 + (height + vSpacing) * (part.row - 1)
    else
        y = Number(rect.attr("y")) + Number(rect.attr("height")) / 2
    return y
}


function calculateXCoordinateOnRect(id, part) {
    let x
    let rect = d3.select(`#${id}`)
    if (part)
        x = Number(rect.attr("x")) + width / 2 + (width + hSpacing) * (part.col - 1)
    else
        x = Number(rect.attr("x")) + Number(rect.attr("width")) / 2
    return x
}


function calculateCoordinatesOnRect(id, part){
    let x, y
    let rect = d3.select(`#${id}`)
    if (part) {
        x = Number(rect.attr("x")) + width / 2 + (width + hSpacing) * (part.col - 1)
        y = Number(rect.attr("y")) + height / 2 + (height + vSpacing) * (part.row - 1)
    }
    else {
        x = Number(rect.attr("x")) + Number(rect.attr("width")) / 2
        y = Number(rect.attr("y")) + Number(rect.attr("height")) / 2
    }
    return {x, y}
}


// Fit a text element into a sibling rect, wrapping the text so that it fits within the shape and additional padding.
function fit(selection, padding) {
    selection.each(function (datum) {
        var text = d3.select(this),
            words = text.text().split(/\s+/).reverse(),
            evaluatedWidth = d3.select(text.node().parentNode).select("rect").attr("width") - 2*padding,
            word,
            line = [],
            lineNumber = 0,
            lineHeight = 1.1, // ems
            x = (Number(text.attr("x")) || 0) + padding,
            y = (Number(text.attr("y")) || 0) + padding,
            // dx = text.attr("dx"),
            // dy = text.attr("dy"),
            dy = 0,
            tspan = text.text(null)
                .append("tspan")
                .attr("x", x)
                .attr("y", y)
                .attr("dy", dy + "em")
                // .attr("dx", dx + "em");
        while (word = words.pop()) {
            line.push(word);
            tspan.text(line.join(" "));
            if (tspan.node().getComputedTextLength() > evaluatedWidth) {
                line.pop();
                tspan.text(line.join(" "));
                line = [word];
                tspan = text.append("tspan")
                    .attr("x", x)
                    .attr("y", y)
                    .attr("dy", ++lineNumber * lineHeight + dy + "em")
                    // .attr("dx", dx + "em")
                    .text(word);
            }
        }
    });
}
