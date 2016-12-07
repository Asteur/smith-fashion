# Back-end Application

## APIサーバーの仕様
<table>
<tr>
<th>url</th>
<th>method</th>
<th>data形式</th>
<th>response</th>
</tr>
<tr>
<td>/</td>
<td>GET</td>
<td>None</td>
<td>Wear This TodayサービスのAPIサーバーです。詳細はhttps://github.com/kdrl/Wear-This-Today より確認ください。</td>
</tr>
<tr>
<td>/api/echo</td>
<td>POST</td>
<td>JSON形式のデータ</td>
<td>JSON形式のデータ(送ったデータが帰ってくる)</td>
</tr>
<tr>
<td>/api/helloworld</td>
<td>POST</td>
<td>
{ "num": 2(定数) }
</td>
<td>
hello world!<br>
hello world!
</td>
</tr>
<tr>
<td>/api/savetest</td>
<td>POST</td>
<td>
any JSON data
</td>
<td>
"saved" or "error"
</td>
</tr>
<tr>
<td>/api/loadtest</td>
<td>GET</td>
<td>
None
</td>
<td>
savetestで保存されたデータの中で一番最新のものが返ってくる
</td>
</tr>
</table>
