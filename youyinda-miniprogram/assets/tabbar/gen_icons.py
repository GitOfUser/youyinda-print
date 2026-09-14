# -*- coding: utf-8 -*-
"""生成优印达小程序 tabBar 图标（81x81 PNG，线性风格）"""
import os
from PIL import Image, ImageDraw

OUT = r"D:\Projects\youyinda-print\youyinda-miniprogram\assets\tabbar"
os.makedirs(OUT, exist_ok=True)

S = 324  # 4x 超采样
W = 26   # 线宽（4x 尺度）
NORMAL = (148, 163, 184, 255)   # #94A3B8
ACTIVE = (79, 70, 229, 255)     # #4F46E5


def canvas():
    img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    return img, ImageDraw.Draw(img)


def rounded_line(d, pts, color, width=W):
    d.line(pts, fill=color, width=width, joint="curve")
    r = width / 2
    for p in (pts[0], pts[-1]):
        d.ellipse([p[0] - r, p[1] - r, p[0] + r, p[1] + r], fill=color)


def draw_home(color):
    img, d = canvas()
    # 屋顶
    rounded_line(d, [(62, 150), (162, 60), (262, 150)], color)
    # 屋身
    rounded_line(d, [(88, 138), (88, 262), (236, 262), (236, 138)], color)
    # 门
    rounded_line(d, [(140, 262), (140, 190), (184, 190), (184, 262)], color, width=W - 4)
    return img


def draw_order(color):
    img, d = canvas()
    # 文档外框（折线形，右上折角）
    outline = [(92, 62), (196, 62), (232, 98), (232, 262), (92, 262), (92, 62)]
    d.line(outline, fill=color, width=W, joint="curve")
    r = W / 2
    for p in outline:
        d.ellipse([p[0] - r, p[1] - r, p[0] + r, p[1] + r], fill=color)
    # 折角小三角
    rounded_line(d, [(196, 62), (196, 98), (232, 98)], color, width=W - 8)
    # 文本线
    for y in (150, 192, 226):
        rounded_line(d, [(122, y), (202, y)], color, width=W - 10)
    return img


def draw_user(color):
    img, d = canvas()
    # 头
    d.ellipse([162 - 52, 48, 162 + 52, 152], outline=color, width=W)
    # 肩
    d.arc([162 - 86, 178, 162 + 86, 178 + 172], start=180, end=360, fill=color, width=W)
    r = W / 2
    d.ellipse([162 - 86 - r, 264 - r, 162 - 86 + r, 264 + r], fill=color)
    d.ellipse([162 + 86 - r, 264 - r, 162 + 86 + r, 264 + r], fill=color)
    return img


def save(img, name):
    img = img.resize((81, 81), Image.LANCZOS)
    img.save(os.path.join(OUT, name), "PNG")
    print("saved", name)


for label, color in (("", NORMAL), ("-active", ACTIVE)):
    save(draw_home(color), f"home{label}.png")
    save(draw_order(color), f"order{label}.png")
    save(draw_user(color), f"user{label}.png")
print("done")
