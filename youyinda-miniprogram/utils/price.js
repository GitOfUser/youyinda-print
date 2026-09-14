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

/**
 * 装订方式价格表
 */
const BINDING_PRICES = {
  none: 0,
  staple: 1.00,
  clip: 0.50,
  glue: 5.00,
  ring: 8.00,
  cover: 3.00
};

/**
 * 装订方式名称映射
 */
const BINDING_NAMES = {
  none: '无装订',
  staple: '订书钉',
  clip: '长尾夹',
  glue: '胶装',
  ring: '圈装',
  cover: '封皮'
};

/**
 * 计算单个打印文件的配置价格
 * 沿用每个文件估算10页的假设
 * @param {Object} config { paperSize, colorType(1黑白/2彩色), duplex(1单面/2双面), copies, binding }
 * @returns {Object} { basePrice, colorPrice, bindingPrice, totalPrice }
 */
function calcItemPrice(config) {
  const {
    paperSize = 'A4',
    colorType = 1,
    duplex = 1,
    copies = 1,
    binding = 'none'
  } = config || {};

  const pagesPerFile = 10;
  let unitPrice = 0.10;

  if (colorType === 2) {
    unitPrice = 0.50;
  }
  if (paperSize === 'A3') {
    unitPrice *= 2;
  }

  let effectivePages = pagesPerFile;
  if (duplex === 2) {
    effectivePages = Math.ceil(pagesPerFile / 2) * 2;
  }

  const basePrice = +(unitPrice * effectivePages).toFixed(2);
  const colorPrice = colorType === 2 ? +(basePrice * 0.8).toFixed(2) : 0;
  const bindingPrice = BINDING_PRICES[binding] !== undefined ? BINDING_PRICES[binding] : 0;
  const singleTotal = +(basePrice + colorPrice + bindingPrice).toFixed(2);
  const totalPrice = +((singleTotal) * copies).toFixed(2);

  return {
    basePrice,
    colorPrice,
    bindingPrice,
    totalPrice
  };
}

module.exports = {
  formatPrice,
  formatPriceWithSymbol,
  calcPrintPrice: calculatePrintPrice,
  calcExpressPrice: calculateExpressPrice,
  calcItemPrice,
  BINDING_PRICES,
  BINDING_NAMES
};
