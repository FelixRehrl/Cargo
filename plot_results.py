import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Load the CSV data
df = pd.read_csv('data/output_stats.csv')

# Verify the DataFrame structure
print(df.head())
print(df.info())

# Ensure 'Duration_sec' is numeric
df['Duration_sec'] = pd.to_numeric(df['Duration_sec'], errors='coerce')

# Drop any rows with NaN in 'Duration_sec'
df.dropna(subset=['Duration_sec'], inplace=True)

# Set the visual style
sns.set(style="whitegrid")

# Aggregate statistics: mean and standard deviation
agg_df = df.groupby(['Algorithm', 'InputFile']).agg(
    mean_duration=('Duration_sec', 'mean'),
    std_duration=('Duration_sec', 'std')
).reset_index()

# Print aggregated data
print(agg_df)

# Create a bar plot with error bars representing standard deviation
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

# Optionally, save the aggregated data
agg_df.to_csv('aggregated_output_stats.csv', index=False)
