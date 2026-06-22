import cairosvg
import os

# 转换SVG为PNG
def convert_svg_to_png(svg_path, png_path):
    try:
        cairosvg.svg2png(url=svg_path, write_to=png_path, output_width=800, output_height=600)
        print(f"转换成功：{png_path}")
        return True
    except Exception as e:
        print(f"转换失败：{e}")
        return False

# 主函数
def main():
    # 图表路径
    charts = [
        ('temp/architecture.svg', 'temp/architecture.png'),
        ('temp/module.svg', 'temp/module.png'),
        ('temp/er.svg', 'temp/er.png'),
        ('temp/user-entity.svg', 'temp/user-entity.png')
    ]
    
    # 转换所有图表
    for svg_path, png_path in charts:
        if os.path.exists(svg_path):
            convert_svg_to_png(svg_path, png_path)
        else:
            print(f"文件不存在：{svg_path}")

if __name__ == "__main__":
    main()