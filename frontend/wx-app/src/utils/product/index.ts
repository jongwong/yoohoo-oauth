export const getFinallyPrice = (data: {
  price?: number;
  final_price?: number;
}) => {
  return data.final_price;
};
