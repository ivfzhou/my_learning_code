# 一、在 Debian 编译安装

运行命令：

```shell
cd ~/src/
git clone git@github.com:git/git.git -o github
sudo apt install libcurl4-openssl-dev zlib1g-dev libssl-dev libexpat1-dev gettext
make clean
make NO_DOC=1 NO_TCLTK=1 prefix=~/programs/git install
```

# 二、设置访问远程仓库账密

1. 开启账密存储：`git config --global credential.helper store`。
2. 用户家目录下文件 `.git-credentials` 添加 `https://user:token@host.com`。`.netrc` 添加 `machine github.com login USERNAME password APIKEY`。

# 三、常用命令

## 1. 公钥密钥

- `ssh-keygen -t rsa -C <email>`：生成 RSA 密钥对。
- `ssh -T git@github.com`：测试与 github 链接。

## 2. 配置

- `git --version`：打印版本号。
- `--system`：全部用户下生效的配置。`--global`：当前用户下生效的配置。默认：当前仓库下生效的配置。
- `git config --global user.name <user>`：全局配置在家目录下 .gitconfig，项目配置在 .git/config。
- `git config --global user.email <email>`
- `git config -e`：打开配置进行编辑。
- `GIT_CONFIG=test.ini git config a.b.c.d 'hello'`：使用 git 编辑 ini 文件。
- `GIT_CONFIG=test.ini git config a.b.c.d`：使用 git 读取 ini 文件配置。
- `init.defaultBranch <name>`：设置初始化仓库默认分支名。
- `http.sslVerify false`
- `git config -l`：查看配置信息。
- `https.proxy https://proxyuser:password@proxyserver:port`
- `http.proxy http://proxyuser:password@proxyserver:port`：配置网络代理。
- `core.quotepath false`：控制非 ASCII 字符在终端的显示方式，设置 true 转义为八进制格式显示。
- `i18n.commitEncoding`：提交内容的编码格式。
- `i18n.logOutputEncoding`：日志输出时的编码格式。
- `aliase.ci "commit -s"`：设置命令别名。
- `color.ui true`：开启颜色展示。

### 2.1 配置 ssh 网络代理

```shell
sudo apt install connect-proxy
vim .ssh/config
```

添加：
```txt
Host github.com
User git
ProxyCommand connect-proxy -S 127.0.0.1:7897 %h %p
```

## 3. 初始化仓库

- `git init <dir>`：初始化仓库。
- `git clone <url> -o <origin> -b <branch> <dir>`：克隆远程仓库。

## 4. 添加与提交

- `git add <file>`：将一个文件添加到暂存区。
- `git add .`：将所有修改和新增文件添加到暂存区。
- `git add -f <file>`：将一个忽略的文件添加到暂存区。
- `git add -u .`：将已追踪文件的修改添加到暂存区。
- `git checkout <filename>`：暂存区覆盖工作区。
- `git checkout .`：暂存区覆盖工作区。
- `git rm --cached -rf <filename>`：删除暂存区文件。
- `git status -s`：显示文件是否新添加到暂存区，是否有修改，是否未追踪。
- `git commit -m <注释>`：暂存区提交到仓库。
- `git commit -am <注释>`：将工作区所有修改的追踪文件提交暂存区和本地仓库。
- `git commit --amend -m <注释>`：重新上次提交。
- `git commit --allow-empty`：空白提交。
- `git reset <filename>`：本地仓库覆盖暂存区。
- `git reset --hard HEAD^`：回退上一个版本。`^^` 或者 `~2`。本地仓库覆盖暂存区和工作区。
- `git reset --hard <commit_id>`：回退到这个版本覆盖三区。
- `git reset --soft <commit_id>`：仓库版本重置。
- `git checkout <origin/branch> .`：工作区应用远程分支修改量。

## 5. 远程分支

- `git remote -v`：查看远程仓库。
- `git remote add <origin> <url>`：添加远程仓库地址。
- `git remote set-url <origin> <newurl>`：设置远程仓库地址。
- `git remote rm <origin>`：删除远程仓库。
- `git remote rename <origin> <newname>`：修改远程仓库名称。
- `git fetch --all`：获取远程仓库数据信息。
- `git ls-remote --heads <remote>`：查看远程分支。
- `git ls-remote --tags <remote>`：查看远程标签。

## 6. 标签

- `git tag --sort=-creatordate -l -n <pattern>`：列出 tag。
- `git tag -d <tag>`：删除 tag。

## 7. 分支

- `git checkout <branchname>`：切换分支。
- `git checkout -b <branchname>`：创建新分支并切换。
- `git branch -v`：查看分支。
- `git branch <branchname>`：创建分支。
- `git branch -d <branchname>`：删除分支。
- `git pull <origin> <branch>`：将远程仓库上的变动拉取到本地库并且合并。
- `git push -u <origin> <branchname>`：提交分支到远程仓库，并关联分支 
- `git push <origin> --tags`：将本地所有 tag 推送给远端仓库。
- `git push <origin> <tagname>`：将本地的 tag 推送给远程仓库。
- `git push <origin> --all`：所有分支推送到远程仓库。
- `git merge <branchname>`：当前分支合并选定分支保留分支信息。

## 8. 日志

- `git reflog`：查看操作记录。
- `git log --pretty=oneline`：查看提交记录。
- `git log --graph --pretty=oneline --abbrev=commit`：查看提交记录。
- `git log --oneline`：查看提交记录。
- `git log --stat`：简单展示提交日志。

## 9. 暂存

- `git stash`：隐藏当前工作区的修改。
- `git stash pop`：将一个隐藏版本恢复并删除。
- `git stash apply`：将一个隐藏版本恢复。
- `git stash list`：查看所有隐藏的版本。

## 10. 比较

- `diff -u file1 file2 > diff.txt`：比较不同。
- `patch -R file2 < diff.txt`：应用差异。
- `git diff <file>`：暂存区与工作区的不同。
- `git diff --cached <file>`：仓库与暂存区的不同。
- `git diff <commitId> <file>`：提交记录与工作区的不同。
- `git diff <a> <b> <file>`：提交记录 a 与提交记录 b 的不同。
- `git diff <branch> <branch> <file>`

## 11. 查看操作提交

- `git revert <commitId>`：回滚到这个提交记录。

- `git ls-tree <commitId>`：查看提交记录下的文件。

- `git ls-tree -l HEAD`：查看 HEAD 指向的目录树。

- `git ls-files -s`：显示暂存区目录树。

- `git cat-file -p <id>`：查看文件内容。

- `git cat-file -t <id>`：查看 ID 类型。

- `git cat-file commit HEAD`：查看 HEAD 对应的提交内容。

- `git cherry-pick <commitId>`： 应用某个提交记录的改动。

- `git rebase <branch>`： 当前分支与 branch 分支变基。

- `git blame <file>`： 显示最后修改人。

- `git rev-parse HEAD`：获取 HEAD 的提交 HASH。

- `git grep '文件内容'`：搜索文件。

- `git rev-parse --git-dir`：显示版本库目录位置。

- `git rev-parse --show-toplevel`：显示工作区根目录。

- `git rev-parse --show-prefix`：相对于工作区根目录的相对目录。

- `git rev-parse --show-cdup`：显示从当前目录后退到工作区的根深度。

- `git rev-parse master`：显示提交 ID。

  

