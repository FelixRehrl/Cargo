import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

df = pd.read_csv('data/output_stats.csv')

print(df.head())
print(df.info())

df['Duration_sec'] = pd.to_numeric(df['Duration_sec'], errors='coerce')

df.dropna(subset=['Duration_sec'], inplace=True)

sns.set(style="whitegrid")

agg_df = df.groupby(['Algorithm', 'InputFile']).agg(
    mean_duration=('Duration_sec', 'mean'),
    std_duration=('Duration_sec', 'std')
).reset_index()

print(agg_df)

plt.figure(figsize=(14, 8))
sns.barplot(
    x='Algorithm',
    y='mean_duration',
    hue='InputFile',
    data=agg_df,
    palette="viridis",
    capsize=0.1,
    errcolor='gray',
    errwidth=1.5
)

plt.title('Average Execution Time with Standard Deviation')
plt.xlabel('Algorithm')
plt.ylabel('Average Duration (seconds)')
plt.xticks(rotation=45)
plt.legend(title='Input File')
plt.tight_layout()
plt.savefig('aggregated_execution_time.png')
plt.show()

agg_df.to_csv('aggregated_output_stats.csv', index=False)
