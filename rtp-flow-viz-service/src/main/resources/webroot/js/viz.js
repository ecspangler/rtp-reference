var nodes = [
    {id: "rtp-debtor-payment-service",           shape: {from: {col: 2, row: 1}, to: {col: 2, row: 1}}, name: "ODFI Payment Service"},
    {id: "rtp-debtor-send-payment",              shape: {from: {col: 3, row: 1}, to: {col: 3, row: 1}}, name: "ODFI Send Payment"},
    {id: "rtp-mock",                             shape: {from: {col: 4, row: 1}, to: {col: 4, row: 3}}, name: "Fed/TCH"},
    {id: "rtp-creditor-receive-payment",         shape: {from: {col: 5, row: 1}, to: {col: 5, row: 1}}, name: "RDFI Receive Payment"},
    {id: "rtp-creditor-payment-confirmation",    shape: {from: {col: 5, row: 3}, to: {col: 5, row: 3}}, name: "RDFI Payment Confirmation"},
    {id: "rtp-creditor-payment-acknowledgement", shape: {from: {col: 5, row: 2}, to: {col: 5, row: 2}}, name: "RDFI Payment Acknowledgement"},
    {id: "rtp-creditor-complete-payment",        shape: {from: {col: 6, row: 3}, to: {col: 6, row: 3}}, name: "RDFI Complete Payment"},
    {id: "rtp-creditor-auditing",                shape: {from: {col: 7, row: 3}, to: {col: 7, row: 3}}, name: "RDFI Auditing"},
    {id: "rtp-creditor-customer-notification",   shape: {from: {col: 7, row: 4}, to: {col: 7, row: 4}}, name: "RDFI Customer Notification"},
    {id: "rtp-debtor-payment-confirmation",      shape: {from: {col: 3, row: 3}, to: {col: 3, row: 3}}, name: "ODFI Payment Confirmation"},
    {id: "rtp-debtor-complete-payment",          shape: {from: {col: 2, row: 3}, to: {col: 2, row: 3}}, name: "ODFI Complete Payment"},
    {id: "rtp-creditor-core-banking",            shape: {from: {col: 7, row: 2}, to: {col: 7, row: 2}}, name: "RDFI Core Banking/Ledger"},
    {id: "rtp-debtor-core-banking",              shape: {from: {col: 1, row: 2}, to: {col: 1, row: 2}}, name: "ODFI Core Banking/Ledger"},
    {id: "rtp-debtor-auditing",                  shape: {from: {col: 1, row: 3}, to: {col: 1, row: 3}}, name: "ODFI Auditing"},
    {id: "rtp-debtor-customer-notification",     shape: {from: {col: 1, row: 4}, to: {col: 1, row: 4}}, name: "ODFI Customer Notification"}
]

var edges = [
    {from: "rtp-debtor-payment-service",           to: "rtp-debtor-send-payment",               topic: "debtor-payments"},
    {from: "rtp-debtor-payment-service",           to: "rtp-debtor-complete-payment",           topic: "debtor-payments"},
    {from: "rtp-debtor-send-payment",              to: "rtp-mock", toPart: {col: 1, row: 1},    topic: "mock-rtp-debtor-credit-transfer"},
    {from: "rtp-mock", fromPart: {col: 1, row: 1}, to: "rtp-creditor-receive-payment",          topic: "mock-rtp-creditor-credit-transfer"},
    {from: "rtp-mock", fromPart: {col: 1, row: 3}, to: "rtp-creditor-payment-confirmation",     topic: "mock-rtp-creditor-confirmation"},
    {from: "rtp-mock", fromPart: {col: 1, row: 3}, to: "rtp-debtor-payment-confirmation",       topic: "mock-rtp-debtor-confirmation"},
    {from: "rtp-creditor-receive-payment",         to: "rtp-creditor-payment-acknowledgement",  topic: "creditor-payments"},
    {from: "rtp-creditor-receive-payment",         to: "rtp-creditor-complete-payment",         topic: "creditor-payments"},
    {from: "rtp-creditor-payment-confirmation",    to: "rtp-creditor-complete-payment",         topic: "creditor-payment-confirmation"},
    {from: "rtp-creditor-payment-acknowledgement", to: "rtp-mock", toPart: {col: 1, row: 2},    topic: "mock-rtp-creditor-acknowledgement"},
    {from: "rtp-creditor-complete-payment",        to: "rtp-creditor-core-banking",             topic: "creditor-completed-payments"},
    {from: "rtp-creditor-complete-payment",        to: "rtp-creditor-auditing",                 topic: "creditor-completed-payments"},
    {from: "rtp-creditor-complete-payment",        to: "rtp-creditor-customer-notification",    topic: "creditor-completed-payments"},
    {from: "rtp-debtor-payment-confirmation",      to: "rtp-debtor-complete-payment",           topic: "debtor-payment-confirmation"},
    {from: "rtp-debtor-complete-payment",          to: "rtp-debtor-core-banking",               topic: "debtor-completed-payments"},
    {from: "rtp-debtor-complete-payment",          to: "rtp-debtor-auditing",                   topic: "debtor-completed-payments"},
    {from: "rtp-debtor-complete-payment",          to: "rtp-debtor-customer-notification",      topic: "debtor-completed-payments"},
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
    .attr("data-topic", edge => edge.topic)


function messageAt(messageId, nodeId, nodePart) {
    let messages = d3.select('#messages')

    let x = calculateXCoordinateOnRect(nodeId, nodePart) - 30
    let y = calculateYCoordinateOnRect(nodeId, nodePart) - 13

    return messages.selectAll(`#${messageId}`)
        .data([messageId])
        .join("use")
        .attr("xlink:href", "#dollarSignDef")
        .attr("id", messageId)
        .attr("data-current-location", nodeId)
        .attr("data-current-location-part", JSON.stringify(nodePart))
        .attr("transform", `translate(${x}, ${y})`)
}

function moveMessage(messageId, edge) {
    let fromNode = messageAt(messageId, edge.from, edge.fromPart)
    fromNode.transition()
        .duration(500)
        .attrTween("transform", function(datum) {
            // TODO: find out how to filter the elements in `this.transform.baseVal` to get just the `translate` transformations
            // this.transform.baseVal.filter(v => v.type === SVGTransform.SVG_TRANSFORM_TRANSLATE) or something like that
            let startX = this.transform.baseVal.getItem(0).matrix.e
            let startY = this.transform.baseVal.getItem(0).matrix.f
            let endX = calculateXCoordinateOnRect(edge.to, edge.toPart) - 30
            let endY = calculateYCoordinateOnRect(edge.to, edge.toPart) - 13
            return t => `translate(${(1-t)*startX + t*endX}, ${(1-t)*startY + t*endY})`
        })
}


function calculateEdgeCoordinates(edge) {
    let x1 = calculateXCoordinateOnRect(edge.from, edge.fromPart)
    let x2 = calculateXCoordinateOnRect(edge.to, edge.toPart)
    let y1 = calculateYCoordinateOnRect(edge.from, edge.fromPart)
    let y2 = calculateYCoordinateOnRect(edge.to, edge.toPart)

    let xDirection = (x2 - x1) / Math.abs(x2 - x1) || 0
    x1 += xDirection * width / 2 // If xDirection is negative (because the line is going backwards) x1 will be moved to the left by half of the width of the rect.
    x2 -= xDirection * width / 2 // Conversely, if the xDirection is negative, x2 will be moved to the right by half of the width of the rect.

    let yDirection = (y2 - y1) / Math.abs(y2 - y1) || 0
    y1 += yDirection * height / 2
    y2 -= yDirection * height / 2

    return {x1, x2, y1, y2}
}


function calculateYCoordinateOnRect(id, part){
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
            dy = 0,
            tspan = text.text(null)
                .append("tspan")
                .attr("x", x)
                .attr("y", y)
                .attr("dy", dy + "em")
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
                    .text(word);
            }
        }
    });
}

// Enable this when running locally and hitting a remote REST endpoint.
// let root = "http://rtp-flow-viz-service-rtp-reference.192.168.42.144.nip.io";
let root = "";
let loop = () => {
    // TODO: Re-enable this when I can figure out how to get long polling working in Vertx
    // fetch(root + "/events?poll=long")
    fetch(root + "/events")
        .then(response => response.json())
        .then(data => {
            data.forEach(event => {
                let eventEdges = edges.filter(edge => edge.topic === event.location)
                if (eventEdges.length > 1) {
                    let eventEdge = eventEdges[0]
                    // messageAt(event.messageId, eventEdge.to)
                    moveMessage(event.messageId, eventEdge)
                }
            })
        })
        .finally(() => setTimeout(loop,2000))
}
setTimeout(loop, 1)

