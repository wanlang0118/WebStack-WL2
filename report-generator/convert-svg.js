const sharp = require('sharp');
const fs = require('fs');
const path = require('path');

// 转换SVG为PNG
async function convertSvgToPng(svgPath, pngPath) {
  try {
    const svgBuffer = fs.readFileSync(svgPath);
    await sharp(svgBuffer)
      .resize(800, 600)
      .png()
      .toFile(pngPath);
    console.log(`转换成功：${pngPath}`);
    return true;
  } catch (error) {
    console.error(`转换失败：${error.message}`);
    return false;
  }
}

// 主函数
async function main() {
  // 图表路径
  const charts = [
    ['temp/architecture.svg', 'temp/architecture.png'],
    ['temp/module.svg', 'temp/module.png'],
    ['temp/er.svg', 'temp/er.png'],
    ['temp/user-entity.svg', 'temp/user-entity.png']
  ];
  
  // 转换所有图表
  for (const [svgPath, pngPath] of charts) {
    if (fs.existsSync(svgPath)) {
      await convertSvgToPng(svgPath, pngPath);
    } else {
      console.log(`文件不存在：${svgPath}`);
    }
  }
}

// 运行主函数
main().catch(console.error);