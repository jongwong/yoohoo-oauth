const isVite = process.env.TARO_BUILD_TYPE === "vite";
module.exports = {
  presets: [
    [
      "taro",
      {
        framework: "react",
        ts: "true",
        compiler: isVite ? "vite" : "webpack5",
      },
    ],
  ],
  plugins: [],
};
