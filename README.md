# metaweblog-api-adaptor
a metaweblog api adaptor

## feature

### confluence支持
- [x] getUsersBlogs
- [x] newPost
- [ ] edtPost

## todo

- [ ] 使用redis保存文章ID，key是 `md文件名` ，value是 `postId` ，标题从元数据中获取，取第一个一级标题，即使 `# 标题` 。