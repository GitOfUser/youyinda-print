function formatPrice(price) {
  if (price === null || price === undefined) {
    return '0.00';
  }
  return parseFloat(price).toFixed(2);
}

function formatPriceWithSymbol(price) {
  return '¥' + formatPrice(price);
}

function calculatePrintPrice(config) {
  const {
    totalPages,
    isDoubleSide,
    colorType,
    paperSize,
    printType,
    isBinding
  } = config;
  
  let unitPrice = 0.10;
  
  if (colorType === 'color') {
    unitPrice = 0.50;
  }
  
  if (paperSize === 'A3') {
    unitPrice *= 2;
  }
  
  let effectivePages = totalPages;
  if (isDoubleSide) {
    effectivePages = Math.ceil(totalPages / 2) * 2;
  }
  
  const printCost = unitPrice * effectivePages;
  const bindingCost = isBinding ? 5.00 : 0;
  const totalPrice = printCost + bindingCost;
  
  return {
    unitPrice: formatPrice(unitPrice),
    effectivePages: effectivePages,
    printCost: formatPrice(printCost),
    bindingCost: formatPrice(bindingCost),
    totalPrice: formatPrice(totalPrice)
  };
}

function calculateExpressPrice(config) {
  const {
    weight,
    expressCode,
    senderProvince,
    receiverProvince
  } = config;
  
  let basePrice = 10.00;
  let continuePrice = 5.00;
  
  const isSameProvince = senderProvince === receiverProvince;
  if (isSameProvince) {
    basePrice = 8.00;
    continuePrice = 3.00;
  }
  
  const firstWeight = 1;
  let totalPrice = basePrice;
  
  if (weight > firstWeight) {
    const continueWeight = Math.ceil(weight - firstWeight);
    totalPrice += continueWeight * continuePrice;
  }
  
  return {
    basePrice: formatPrice(basePrice),
    continuePrice: formatPrice(continuePrice),
    totalPrice: formatPrice(totalPrice)
  };
}

module.exports = {
  formatPrice,
  formatPriceWithSymbol,
  calcPrintPrice: calculatePrintPrice,
  calcExpressPrice: calculateExpressPrice
};
