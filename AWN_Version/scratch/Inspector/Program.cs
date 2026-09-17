using System;
using System.IO;
using System.IO.Compression;

namespace Inspector
{
    class Program
    {
        static void Main(string[] args)
        {
            string zipPath = @"C:\Users\vtson\Downloads\AWN_Version\AWN_Version\backupsrc\src_backup_20260916_102916.zip";
            string currentSrc = @"C:\Users\vtson\Downloads\AWN_Version\AWN_Version\src";

            using (var archive = ZipFile.OpenRead(zipPath))
            {
                Console.WriteLine($"Entries in zip: {archive.Entries.Count}");
                int diff = 0;
                foreach (var entry in archive.Entries)
                {
                    if (string.IsNullOrEmpty(entry.Name)) continue;
                    string target = Path.Combine(currentSrc, entry.FullName);
                    if (!File.Exists(target))
                    {
                        Console.WriteLine("Missing in current: " + entry.FullName);
                        diff++;
                    }
                    else
                    {
                        var fi = new FileInfo(target);
                        if (fi.Length != entry.Length)
                        {
                            Console.WriteLine($"Diff size in {entry.FullName}: zip={entry.Length} vs curr={fi.Length}");
                            diff++;
                        }
                    }
                }
                Console.WriteLine($"Total differences: {diff}");
            }
        }
    }
}
