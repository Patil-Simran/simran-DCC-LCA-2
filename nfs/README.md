# Task 2 — NFS (Linux server + client)

for mount use **`127.0.0.1`** instead of `SERVER_IP` (e.g. `sudo mount -t nfs 127.0.0.1:/srv/nfs/share /mnt/nfs_share`). You can use one terminal for server commands and another for client/mount — still one machine.

## Steps — server

1. `sudo apt update && sudo apt install -y nfs-kernel-server`
2. `sudo mkdir -p /srv/nfs/share && sudo chown nobody:nogroup /srv/nfs/share`
3. `echo "Hello from NFS server" | sudo tee /srv/nfs/share/hello.txt`
4. `sudo nano /etc/exports` — add one line:
   - Simple: `/srv/nfs/share *(rw,sync,no_subtree_check,no_root_squash)`
   - Or: `/srv/nfs/share CLIENT_IP(rw,sync,no_subtree_check)`
5. `sudo exportfs -ra && sudo systemctl restart nfs-kernel-server`
6. If `ufw` on: `sudo ufw allow from CLIENT_IP to any port nfs` and port `111` (or ask instructor).

## Steps — client

1. `sudo apt update && sudo apt install -y nfs-common`
2. `sudo mkdir -p /mnt/nfs_share`
3. `sudo mount -t nfs SERVER_IP:/srv/nfs/share /mnt/nfs_share`
4. `mount | grep nfs` — `ls /mnt/nfs_share` — `cat /mnt/nfs_share/hello.txt`
5. `echo "Written from client" | sudo tee /mnt/nfs_share/from_client.txt`
6. On **server:** `ls /srv/nfs/share` — `cat /srv/nfs/share/from_client.txt`

## Optional

- `showmount -e SERVER_IP` (from client)
- Persistent: add `SERVER_IP:/srv/nfs/share /mnt/nfs_share nfs defaults,_netdev 0 0` to `/etc/fstab`, then `sudo mount -a`
- Unmount: `sudo umount /mnt/nfs_share`
