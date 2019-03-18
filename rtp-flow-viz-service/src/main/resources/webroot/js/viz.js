var services = {
  "rtp-debtor-payment-service": {
    to: ["rtp-debtor-send-payment", "rtp-debtor-complete-payment"],
    shape: [{
      from: {x:1,y:1},
      to: {x:1,y:1}
    }],
    name: "ODFI Payment Service"
  },
  "rtp-debtor-send-payment": {
    to: ["rtp-mock"],
    shape: [{
      from: {x:2,y:1},
      to: {x:2,y:1}
    }],
    name: "ODFI Send Payment"
  },
  "rtp-mock": {
    to: ["rtp-creditor-receive-payment", "rtp-creditor-payment-confirmation", "rtp-debtor-payment-confirmation"],
    shape: [{
      from: {x:3,y:1},
      to: {x:3,y:3}
    }],
    name: "Fed/TCH"
  },
  "rtp-creditor-receive-payment": {
    to: ["rtp-creditor-payment-acknowledgement", "rtp-creditor-complete-payment"],
    shape: [{
      from: {x:4,y:1},
      to: {x:4,y:1}
    }],
    name: "RDFI Receive Payment"
  },
  "rtp-creditor-payment-confirmation": {
    to: ["rtp-creditor-complete-payment"],
    shape: [{
      from: {x:4,y:3},
      to: {x:4,y:3}
    }],
    name: "RDFI Payment Confirmation`"
  },
  "rtp-creditor-payment-acknowledgement": {
    to: ["rtp-mock"],
    shape: [{
      from: {x:4,y:2},
      to: {x:4,y:2}
    }],
    name: "RDFI Payment Acknowledgement"
  },
  "rtp-creditor-complete-payment": {
    to: ["rtp-creditor-core-banking"],
    shape: [{
      from: {x:5,y:3},
      to: {x:5,y:3}
    }],
    name: "RDFI Core Banking/Ledger"
  },
  "rtp-debtor-payment-confirmation": {
    to: ["rtp-debtor-complete-payment"],
    shape: [{
      from: {x:2,y:3},
      to: {x:2,y:3}
    }],
    name: "ODFI Payment Confirmation"
  },
  "rtp-debtor-complete-payment": {
    to: ["rtp-debtor-core-banking"],
    shape: [{
      from: {x:1,y:3},
      to: {x:1,y:3}
    }],
    name: "ODFI Complete Payment"
  }
};

let width = 200;
let height = 100;
let hSpacing = 100;
let vSpacing = 50;
let viz = d3.select("#viz")

let gNode = viz.append("g")
  .attr("id", "nodes")
  .selectAll("g")
  .data(Object.keys(services))
  .enter().append('g')

gNode.append("rect")
  .attr("x", function(name) {
    let shape = services[name].shape[0];
    return (shape.from.x - 1) * hSpacing + (shape.from.x - 1) * width;
  })
  .attr("y", function(name) {
    let shape = services[name].shape[0];
    return (shape.from.y - 1) * vSpacing + (shape.from.y - 1) * height;
  })
  .attr("height", function(name) {
    let shape = services[name].shape[0];
    return (shape.to.y - shape.from.y + 1) * height + (shape.to.y - shape.from.y) * vSpacing;
  })
  .attr("width", function(name) {
    let shape = services[name].shape[0];
    return (shape.to.x - shape.from.x + 1) * width + (shape.to.x - shape.from.x) * hSpacing;
  })
  .style("fill", "lightblue")
  .style("stroke", "blue")
  .attr("id", name => name)

gNode.append("text")
  .text(key => services[key].name)
  .attr('font-size', 15)
  .attr('dx', 10)
  .attr('dy', 25)
  .attr('x', key => d3.select("#"+key).attr("x"))
  .attr('y', key => d3.select("#"+key).attr("y"))
