---
description: Git commit ve push işlemi (onay gerektirir)
---

## Git Push Workflow

1. Değişiklikleri stage'e ekle:
```
git add .
```

2. Commit at (mesajı kullanıcıdan al):
```
git commit -m "<COMMIT_MESSAGE>"
```

3. Remote'a push et:
```
git push origin main
```

> ⚠️ Bu workflow'da turbo yok, her adımda onay istenir.
