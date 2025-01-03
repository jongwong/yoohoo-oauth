const isVite = process.argv.includes("--vite");

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
