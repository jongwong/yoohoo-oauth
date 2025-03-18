import { isNumber } from "lodash-es";

export const getFinallyPrice = (data: {
  price?: number;
  final_price?: number;
}) => {
  if (isNumber(data.final_price)) {
    return data.final_price;
  }

  return data?.price || 0;
};
