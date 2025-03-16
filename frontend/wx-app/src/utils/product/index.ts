import { isNumber } from "lodash-es";

export const getFinallyPrice = (data: {
  price?: number;
  discount_price?: number;
  cost_price?: number;
}) => {
  if (isNumber(data.discount_price)) {
    return data.discount_price;
  }
  return data.price;
};
