<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<link rel="stylesheet"
	href="/gerbil/webjars/bootstrap/3.2.0/css/bootstrap.min.css">
<link rel="stylesheet"
	href="/gerbil/webjars/bootstrap-multiselect/0.9.8/css/bootstrap-multiselect.css" />
<link rel="icon" type="image/png" href="/gerbil/webResources/gerbilicon_transparent.png">
	<script type="text/javascript"
			src="/gerbil/webjars/jquery/2.1.1/jquery.min.js"></script>

</head>
<body class="container">
	<%@include file="navbar.jsp"%>
	<h1>WebNLG2020 challenge final results.</h1>

    <h2>WebNLG RDF2Text</h2>

    <div class="tab-content" id="myTabContent">
      <h3>English</h3>
      <div class="tab-pane show active" id="rdf2text-en" role="tabpanel" aria-labelledby="rdf2text-en-tab">
        <h4>All</h4>
            <table class="table table-hover table-condensed">
            <tr><th>SYSTEM ID</th><th>BLEU</th><th>BLEU_NLTK</th><th>METEOR</th><th>CHRF++</th><th>TER</th><th>BERT_PRECISION</th><th>BERT_RECALL</th><th>BERT_F1</th><th>BLEURT</th></tr>
            <tr><td>id18</td><td>53.98</td><td>0.535</td><td>0.417</td><td>0.690</td><td>0.406</td><td>0.960</td><td>0.957</td><td>0.958</td><td>0.62</td></tr>
            <tr><td>id30</td><td>53.54</td><td>0.532</td><td>0.414</td><td>0.688</td><td>0.416</td><td>0.958</td><td>0.955</td><td>0.956</td><td>0.61</td></tr>
            <tr><td>id30_1</td><td>52.07</td><td>0.518</td><td>0.413</td><td>0.685</td><td>0.444</td><td>0.955</td><td>0.954</td><td>0.954</td><td>0.58</td></tr>
            <tr><td>id34</td><td>52.67</td><td>0.523</td><td>0.413</td><td>0.686</td><td>0.423</td><td>0.957</td><td>0.955</td><td>0.956</td><td>0.6</td></tr>
            <tr><td>id36_1</td><td>52.67</td><td>0.523</td><td>0.413</td><td>0.686</td><td>0.423</td><td>0.957</td><td>0.955</td><td>0.956</td><td>0.6</td></tr>
            <tr><td>id35_1</td><td>52.67</td><td>0.523</td><td>0.413</td><td>0.686</td><td>0.423</td><td>0.957</td><td>0.955</td><td>0.956</td><td>0.6</td></tr>
            <tr><td>id5</td><td>51.74</td><td>0.517</td><td>0.411</td><td>0.679</td><td>0.435</td><td>0.955</td><td>0.954</td><td>0.954</td><td>0.6</td></tr>
            <tr><td>id36</td><td>51.59</td><td>0.512</td><td>0.409</td><td>0.681</td><td>0.431</td><td>0.956</td><td>0.954</td><td>0.954</td><td>0.59</td></tr>
            <tr><td>id35</td><td>51.59</td><td>0.512</td><td>0.409</td><td>0.681</td><td>0.431</td><td>0.956</td><td>0.954</td><td>0.954</td><td>0.59</td></tr>
            <tr><td>id23</td><td>51.74</td><td>0.514</td><td>0.403</td><td>0.669</td><td>0.417</td><td>0.959</td><td>0.954</td><td>0.956</td><td>0.61</td></tr>
            <tr><td>id2</td><td>50.34</td><td>0.500</td><td>0.398</td><td>0.666</td><td>0.435</td><td>0.954</td><td>0.950</td><td>0.951</td><td>0.57</td></tr>
            <tr><td>id15</td><td>40.73</td><td>0.405</td><td>0.393</td><td>0.646</td><td>0.511</td><td>0.940</td><td>0.946</td><td>0.943</td><td>0.45</td></tr>
            <tr><td>id16</td><td>40.73</td><td>0.405</td><td>0.393</td><td>0.646</td><td>0.511</td><td>0.940</td><td>0.946</td><td>0.943</td><td>0.45</td></tr>
            <tr><td>id28</td><td>44.56</td><td>0.432</td><td>0.387</td><td>0.637</td><td>0.479</td><td>0.949</td><td>0.949</td><td>0.948</td><td>0.54</td></tr>
            <tr><td>id12</td><td>40.29</td><td>0.393</td><td>0.386</td><td>0.634</td><td>0.504</td><td>0.944</td><td>0.944</td><td>0.944</td><td>0.45</td></tr>
            <tr><td>id11</td><td>39.84</td><td>0.388</td><td>0.384</td><td>0.632</td><td>0.517</td><td>0.943</td><td>0.943</td><td>0.942</td><td>0.43</td></tr>
            <tr><td>id4</td><td>50.93</td><td>0.482</td><td>0.384</td><td>0.636</td><td>0.454</td><td>0.952</td><td>0.947</td><td>0.949</td><td>0.54</td></tr>
            <tr><td>id26</td><td>50.43</td><td>0.476</td><td>0.382</td><td>0.637</td><td>0.439</td><td>0.955</td><td>0.949</td><td>0.951</td><td>0.57</td></tr>
            <tr><td>id26_1</td><td>50.43</td><td>0.476</td><td>0.382</td><td>0.637</td><td>0.439</td><td>0.955</td><td>0.949</td><td>0.951</td><td>0.57</td></tr>
            <tr><td>baseline</td><td>40.57</td><td>0.396</td><td>0.373</td><td>0.621</td><td>0.517</td><td>0.946</td><td>0.941</td><td>0.943</td><td>0.47</td></tr>
            <tr><td>id17</td><td>39.55</td><td>0.387</td><td>0.372</td><td>0.613</td><td>0.536</td><td>0.935</td><td>0.937</td><td>0.935</td><td>0.37</td></tr>
            <tr><td>id31</td><td>39.55</td><td>0.387</td><td>0.372</td><td>0.613</td><td>0.536</td><td>0.935</td><td>0.937</td><td>0.935</td><td>0.37</td></tr>
            <tr><td>id31_2</td><td>41.03</td><td>0.405</td><td>0.367</td><td>0.608</td><td>0.522</td><td>0.936</td><td>0.935</td><td>0.935</td><td>0.39</td></tr>
            <tr><td>id21</td><td>31.98</td><td>0.313</td><td>0.350</td><td>0.545</td><td>0.629</td><td>0.920</td><td>0.922</td><td>0.920</td><td>0.4</td></tr>
            <tr><td>id13_11</td><td>38.37</td><td>0.377</td><td>0.343</td><td>0.584</td><td>0.587</td><td>0.927</td><td>0.922</td><td>0.924</td><td>0.33</td></tr>
            <tr><td>id24</td><td>39.12</td><td>0.379</td><td>0.337</td><td>0.579</td><td>0.564</td><td>0.933</td><td>0.927</td><td>0.929</td><td>0.37</td></tr>
            <tr><td>id14</td><td>39.12</td><td>0.379</td><td>0.337</td><td>0.579</td><td>0.564</td><td>0.933</td><td>0.927</td><td>0.929</td><td>0.37</td></tr>
            <tr><td>id13_1</td><td>38.2</td><td>0.376</td><td>0.335</td><td>0.571</td><td>0.577</td><td>0.920</td><td>0.920</td><td>0.920</td><td>0.29</td></tr>
            <tr><td>id13</td><td>38.2</td><td>0.376</td><td>0.335</td><td>0.571</td><td>0.577</td><td>0.920</td><td>0.920</td><td>0.920</td><td>0.29</td></tr>
            <tr><td>id13_3</td><td>39.19</td><td>0.387</td><td>0.334</td><td>0.569</td><td>0.565</td><td>0.923</td><td>0.921</td><td>0.922</td><td>0.28</td></tr>
            <tr><td>id13_4</td><td>38.85</td><td>0.383</td><td>0.332</td><td>0.569</td><td>0.573</td><td>0.922</td><td>0.920</td><td>0.921</td><td>0.3</td></tr>
            <tr><td>id13_2</td><td>38.01</td><td>0.375</td><td>0.331</td><td>0.565</td><td>0.583</td><td>0.921</td><td>0.919</td><td>0.920</td><td>0.27</td></tr>
            <tr><td>id13_6</td><td>37.96</td><td>0.374</td><td>0.331</td><td>0.566</td><td>0.580</td><td>0.918</td><td>0.919</td><td>0.918</td><td>0.28</td></tr>
            <tr><td>id10</td><td>22.84</td><td>0.217</td><td>0.326</td><td>0.534</td><td>0.696</td><td>0.906</td><td>0.907</td><td>0.906</td><td>-0.03</td></tr>
            <tr><td>id13_7</td><td>36.07</td><td>0.356</td><td>0.324</td><td>0.555</td><td>0.599</td><td>0.917</td><td>0.915</td><td>0.916</td><td>0.24</td></tr>
            <tr><td>id13_8</td><td>36.6</td><td>0.361</td><td>0.322</td><td>0.554</td><td>0.594</td><td>0.918</td><td>0.915</td><td>0.916</td><td>0.25</td></tr>
            <tr><td>id20</td><td>31.26</td><td>0.304</td><td>0.316</td><td>0.542</td><td>0.659</td><td>0.924</td><td>0.925</td><td>0.923</td><td>0.31</td></tr>
            <tr><td>id26</td><td>27.5</td><td>0.272</td><td>0.305</td><td>0.519</td><td>0.846</td><td>0.895</td><td>0.907</td><td>0.900</td><td>0.03</td></tr>
            <tr><td>id13_9</td><td>28.71</td><td>0.280</td><td>0.243</td><td>0.448</td><td>0.689</td><td>0.889</td><td>0.884</td><td>0.886</td><td>-0.09</td></tr>
            <tr><td>id13_10</td><td>30.79</td><td>0.278</td><td>0.239</td><td>0.439</td><td>0.677</td><td>0.893</td><td>0.884</td><td>0.888</td><td>-0.09</td></tr>
            <tr><td>id13_5</td><td>23.08</td><td>0.230</td><td>0.225</td><td>0.421</td><td>0.743</td><td>0.877</td><td>0.879</td><td>0.877</td><td>-0.19</td></tr>
            <tr><td>id17_1</td><td>24.45</td><td>0.240</td><td>0.223</td><td>0.425</td><td>0.739</td><td>0.874</td><td>0.880</td><td>0.876</td><td>-0.22</td></tr>
            <tr><td>id31_1</td><td>24.45</td><td>0.240</td><td>0.223</td><td>0.425</td><td>0.739</td><td>0.874</td><td>0.880</td><td>0.876</td><td>-0.22</td></tr>
            </table>
        <h4>Seen Categories</h4>
            <table class="table table-hover table-condensed">
            <tr><th>SYSTEM ID</th><th>BLEU</th><th>BLEU_NLTK</th><th>METEOR</th><th>CHRF++</th><th>TER</th><th>BERT_PRECISION</th><th>BERT_RECALL</th><th>BERT_F1</th><th>BLEURT</th></tr>
            <tr><td>id34</td><td>61.33</td><td>0.608</td><td>0.436</td><td>0.730</td><td>0.395</td><td>0.964</td><td>0.961</td><td>0.962</td><td>0.61</td></tr>
            <tr><td>id36</td><td>61.33</td><td>0.608</td><td>0.436</td><td>0.730</td><td>0.395</td><td>0.964</td><td>0.961</td><td>0.962</td><td>0.61</td></tr>
            <tr><td>id36_1</td><td>61.33</td><td>0.608</td><td>0.436</td><td>0.730</td><td>0.395</td><td>0.964</td><td>0.961</td><td>0.962</td><td>0.61</td></tr>
            <tr><td>id35</td><td>61.33</td><td>0.608</td><td>0.436</td><td>0.730</td><td>0.395</td><td>0.964</td><td>0.961</td><td>0.962</td><td>0.61</td></tr>
            <tr><td>id35_1</td><td>61.33</td><td>0.608</td><td>0.436</td><td>0.730</td><td>0.395</td><td>0.964</td><td>0.961</td><td>0.962</td><td>0.61</td></tr>
            <tr><td>id30</td><td>61.24</td><td>0.607</td><td>0.434</td><td>0.727</td><td>0.393</td><td>0.964</td><td>0.960</td><td>0.962</td><td>0.61</td></tr>
            <tr><td>id18</td><td>60.35</td><td>0.596</td><td>0.434</td><td>0.723</td><td>0.404</td><td>0.964</td><td>0.961</td><td>0.962</td><td>0.59</td></tr>
            <tr><td>id5</td><td>61.08</td><td>0.611</td><td>0.433</td><td>0.725</td><td>0.391</td><td>0.965</td><td>0.961</td><td>0.963</td><td>0.6</td></tr>
            <tr><td>id30_1</td><td>60.11</td><td>0.597</td><td>0.432</td><td>0.724</td><td>0.409</td><td>0.963</td><td>0.960</td><td>0.961</td><td>0.59</td></tr>
            <tr><td>id13_11</td><td>59.32</td><td>0.584</td><td>0.428</td><td>0.712</td><td>0.415</td><td>0.963</td><td>0.957</td><td>0.960</td><td>0.6</td></tr>
            <tr><td>id13_1</td><td>58.81</td><td>0.581</td><td>0.427</td><td>0.713</td><td>0.403</td><td>0.963</td><td>0.958</td><td>0.961</td><td>0.6</td></tr>
            <tr><td>id13</td><td>58.81</td><td>0.581</td><td>0.427</td><td>0.713</td><td>0.403</td><td>0.963</td><td>0.958</td><td>0.961</td><td>0.6</td></tr>
            <tr><td>id13_3</td><td>58.29</td><td>0.578</td><td>0.423</td><td>0.703</td><td>0.408</td><td>0.963</td><td>0.958</td><td>0.960</td><td>0.61</td></tr>
            <tr><td>id13_2</td><td>58.36</td><td>0.580</td><td>0.422</td><td>0.703</td><td>0.408</td><td>0.963</td><td>0.957</td><td>0.960</td><td>0.61</td></tr>
            <tr><td>id2</td><td>59.13</td><td>0.588</td><td>0.422</td><td>0.712</td><td>0.403</td><td>0.964</td><td>0.957</td><td>0.960</td><td>0.58</td></tr>
            <tr><td>id13_6</td><td>57.49</td><td>0.571</td><td>0.420</td><td>0.702</td><td>0.415</td><td>0.962</td><td>0.957</td><td>0.959</td><td>0.59</td></tr>
            <tr><td>id13_4</td><td>58.03</td><td>0.578</td><td>0.420</td><td>0.701</td><td>0.401</td><td>0.964</td><td>0.958</td><td>0.961</td><td>0.61</td></tr>
            <tr><td>id13_7</td><td>57.3</td><td>0.569</td><td>0.417</td><td>0.701</td><td>0.422</td><td>0.961</td><td>0.955</td><td>0.958</td><td>0.58</td></tr>
            <tr><td>id13_8</td><td>56.94</td><td>0.566</td><td>0.417</td><td>0.701</td><td>0.419</td><td>0.963</td><td>0.956</td><td>0.959</td><td>0.58</td></tr>
            <tr><td>id23</td><td>58.26</td><td>0.579</td><td>0.416</td><td>0.699</td><td>0.408</td><td>0.964</td><td>0.958</td><td>0.960</td><td>0.6</td></tr>
            <tr><td>id13_5</td><td>57.63</td><td>0.576</td><td>0.411</td><td>0.697</td><td>0.423</td><td>0.960</td><td>0.955</td><td>0.957</td><td>0.56</td></tr>
            <tr><td>id21</td><td>56.18</td><td>0.550</td><td>0.409</td><td>0.700</td><td>0.430</td><td>0.961</td><td>0.957</td><td>0.958</td><td>0.58</td></tr>
            <tr><td>id15</td><td>46.26</td><td>0.452</td><td>0.406</td><td>0.675</td><td>0.523</td><td>0.944</td><td>0.949</td><td>0.946</td><td>0.42</td></tr>
            <tr><td>id16</td><td>46.26</td><td>0.452</td><td>0.406</td><td>0.675</td><td>0.523</td><td>0.944</td><td>0.949</td><td>0.946</td><td>0.42</td></tr>
            <tr><td>id17</td><td>49.68</td><td>0.482</td><td>0.402</td><td>0.674</td><td>0.504</td><td>0.950</td><td>0.949</td><td>0.949</td><td>0.46</td></tr>
            <tr><td>id31</td><td>49.68</td><td>0.482</td><td>0.402</td><td>0.674</td><td>0.504</td><td>0.950</td><td>0.949</td><td>0.949</td><td>0.46</td></tr>
            <tr><td>id13_9</td><td>55.24</td><td>0.547</td><td>0.401</td><td>0.680</td><td>0.448</td><td>0.955</td><td>0.951</td><td>0.952</td><td>0.56</td></tr>
            <tr><td>id13_10</td><td>54.78</td><td>0.545</td><td>0.399</td><td>0.676</td><td>0.436</td><td>0.957</td><td>0.951</td><td>0.953</td><td>0.56</td></tr>
            <tr><td>id28</td><td>47.36</td><td>0.458</td><td>0.394</td><td>0.654</td><td>0.490</td><td>0.951</td><td>0.950</td><td>0.950</td><td>0.5</td></tr>
            <tr><td>id12</td><td>43.66</td><td>0.417</td><td>0.394</td><td>0.652</td><td>0.530</td><td>0.947</td><td>0.949</td><td>0.948</td><td>0.42</td></tr>
            <tr><td>id11</td><td>42.4</td><td>0.404</td><td>0.392</td><td>0.651</td><td>0.559</td><td>0.945</td><td>0.947</td><td>0.945</td><td>0.37</td></tr>
            <tr><td>id31_2</td><td>52.93</td><td>0.521</td><td>0.391</td><td>0.661</td><td>0.457</td><td>0.955</td><td>0.945</td><td>0.949</td><td>0.49</td></tr>
            <tr><td>baseline</td><td>42.95</td><td>0.415</td><td>0.387</td><td>0.650</td><td>0.563</td><td>0.945</td><td>0.942</td><td>0.943</td><td>0.41</td></tr>
            <tr><td>id17_1</td><td>51.85</td><td>0.512</td><td>0.383</td><td>0.651</td><td>0.464</td><td>0.954</td><td>0.945</td><td>0.949</td><td>0.5</td></tr>
            <tr><td>id31_1</td><td>51.85</td><td>0.512</td><td>0.383</td><td>0.651</td><td>0.464</td><td>0.954</td><td>0.945</td><td>0.949</td><td>0.5</td></tr>
            <tr><td>id26</td><td>60.99</td><td>0.518</td><td>0.381</td><td>0.641</td><td>0.432</td><td>0.961</td><td>0.948</td><td>0.954</td><td>0.55</td></tr>
            <tr><td>id26_1</td><td>60.99</td><td>0.518</td><td>0.381</td><td>0.641</td><td>0.432</td><td>0.961</td><td>0.948</td><td>0.954</td><td>0.55</td></tr>
            <tr><td>id4</td><td>60.53</td><td>0.522</td><td>0.380</td><td>0.638</td><td>0.458</td><td>0.955</td><td>0.946</td><td>0.950</td><td>0.51</td></tr>
            <tr><td>id24</td><td>51.21</td><td>0.495</td><td>0.373</td><td>0.648</td><td>0.478</td><td>0.957</td><td>0.943</td><td>0.949</td><td>0.5</td></tr>
            <tr><td>id14</td><td>51.21</td><td>0.495</td><td>0.373</td><td>0.648</td><td>0.478</td><td>0.957</td><td>0.943</td><td>0.949</td><td>0.5</td></tr>
            <tr><td>id20</td><td>45.05</td><td>0.406</td><td>0.347</td><td>0.601</td><td>0.576</td><td>0.944</td><td>0.937</td><td>0.940</td><td>0.43</td></tr>
            <tr><td>id10</td><td>26.91</td><td>0.247</td><td>0.330</td><td>0.551</td><td>0.714</td><td>0.902</td><td>0.903</td><td>0.902</td><td>-0.15</td></tr>
            <tr><td>id26</td><td>38.9</td><td>0.380</td><td>0.307</td><td>0.538</td><td>0.763</td><td>0.913</td><td>0.909</td><td>0.910</td><td>0.12</td></tr>
            </table>
        <h4>Unseen Categories</h4>
            <table class="table table-hover table-condensed">
            <tr><th>SYSTEM ID</th><th>BLEU</th><th>BLEU_NLTK</th><th>METEOR</th><th>CHRF++</th><th>TER</th><th>BERT_PRECISION</th><th>BERT_RECALL</th><th>BERT_F1</th><th>BLEURT</th></tr>
            <tr><td>id18</td><td>49.15</td><td>0.491</td><td>0.404</td><td>0.660</td><td>0.413</td><td>0.957</td><td>0.953</td><td>0.954</td><td>0.6</td></tr>
            <tr><td>id30_1</td><td>45.34</td><td>0.455</td><td>0.398</td><td>0.651</td><td>0.471</td><td>0.949</td><td>0.949</td><td>0.948</td><td>0.55</td></tr>
            <tr><td>id30</td><td>47.4</td><td>0.474</td><td>0.397</td><td>0.652</td><td>0.437</td><td>0.953</td><td>0.951</td><td>0.951</td><td>0.57</td></tr>
            <tr><td>id34</td><td>46.2</td><td>0.463</td><td>0.394</td><td>0.647</td><td>0.444</td><td>0.951</td><td>0.949</td><td>0.950</td><td>0.57</td></tr>
            <tr><td>id36_1</td><td>46.2</td><td>0.463</td><td>0.394</td><td>0.647</td><td>0.444</td><td>0.951</td><td>0.949</td><td>0.950</td><td>0.57</td></tr>
            <tr><td>id35_1</td><td>46.2</td><td>0.463</td><td>0.394</td><td>0.647</td><td>0.444</td><td>0.951</td><td>0.949</td><td>0.950</td><td>0.57</td></tr>
            <tr><td>id5</td><td>43.98</td><td>0.441</td><td>0.393</td><td>0.636</td><td>0.470</td><td>0.948</td><td>0.948</td><td>0.947</td><td>0.56</td></tr>
            <tr><td>id23</td><td>45.57</td><td>0.454</td><td>0.388</td><td>0.632</td><td>0.438</td><td>0.953</td><td>0.949</td><td>0.950</td><td>0.58</td></tr>
            <tr><td>id36</td><td>43.84</td><td>0.440</td><td>0.387</td><td>0.637</td><td>0.458</td><td>0.949</td><td>0.947</td><td>0.947</td><td>0.55</td></tr>
            <tr><td>id35</td><td>43.84</td><td>0.440</td><td>0.387</td><td>0.637</td><td>0.458</td><td>0.949</td><td>0.947</td><td>0.947</td><td>0.55</td></tr>
            <tr><td>id15</td><td>35.85</td><td>0.364</td><td>0.384</td><td>0.617</td><td>0.512</td><td>0.935</td><td>0.942</td><td>0.938</td><td>0.42</td></tr>
            <tr><td>id16</td><td>35.85</td><td>0.364</td><td>0.384</td><td>0.617</td><td>0.512</td><td>0.935</td><td>0.942</td><td>0.938</td><td>0.42</td></tr>
            <tr><td>id28</td><td>40.87</td><td>0.405</td><td>0.379</td><td>0.615</td><td>0.486</td><td>0.945</td><td>0.946</td><td>0.945</td><td>0.54</td></tr>
            <tr><td>id4</td><td>43.82</td><td>0.436</td><td>0.379</td><td>0.618</td><td>0.472</td><td>0.947</td><td>0.944</td><td>0.945</td><td>0.5</td></tr>
            <tr><td>id26</td><td>43.07</td><td>0.430</td><td>0.376</td><td>0.618</td><td>0.456</td><td>0.949</td><td>0.945</td><td>0.946</td><td>0.54</td></tr>
            <tr><td>id26_1</td><td>43.07</td><td>0.430</td><td>0.376</td><td>0.618</td><td>0.456</td><td>0.949</td><td>0.945</td><td>0.946</td><td>0.54</td></tr>
            <tr><td>id12</td><td>35.86</td><td>0.360</td><td>0.375</td><td>0.606</td><td>0.507</td><td>0.940</td><td>0.939</td><td>0.939</td><td>0.42</td></tr>
            <tr><td>id2</td><td>42.24</td><td>0.425</td><td>0.375</td><td>0.617</td><td>0.460</td><td>0.946</td><td>0.942</td><td>0.943</td><td>0.52</td></tr>
            <tr><td>id11</td><td>35.3</td><td>0.354</td><td>0.373</td><td>0.604</td><td>0.515</td><td>0.939</td><td>0.938</td><td>0.938</td><td>0.39</td></tr>
            <tr><td>baseline</td><td>37.56</td><td>0.370</td><td>0.357</td><td>0.584</td><td>0.510</td><td>0.944</td><td>0.936</td><td>0.940</td><td>0.44</td></tr>
            <tr><td>id17</td><td>29.13</td><td>0.293</td><td>0.345</td><td>0.553</td><td>0.575</td><td>0.922</td><td>0.926</td><td>0.924</td><td>0.23</td></tr>
            <tr><td>id31</td><td>29.13</td><td>0.293</td><td>0.345</td><td>0.553</td><td>0.575</td><td>0.922</td><td>0.926</td><td>0.924</td><td>0.23</td></tr>
            <tr><td>id31_2</td><td>29.39</td><td>0.296</td><td>0.345</td><td>0.553</td><td>0.573</td><td>0.922</td><td>0.926</td><td>0.924</td><td>0.23</td></tr>
            <tr><td>id10</td><td>20.67</td><td>0.202</td><td>0.325</td><td>0.523</td><td>0.693</td><td>0.908</td><td>0.909</td><td>0.908</td><td>0.02</td></tr>
            <tr><td>id24</td><td>29.46</td><td>0.288</td><td>0.316</td><td>0.526</td><td>0.608</td><td>0.918</td><td>0.917</td><td>0.917</td><td>0.18</td></tr>
            <tr><td>id14</td><td>29.46</td><td>0.288</td><td>0.316</td><td>0.526</td><td>0.608</td><td>0.918</td><td>0.917</td><td>0.917</td><td>0.18</td></tr>
            <tr><td>id21</td><td>16.2</td><td>0.161</td><td>0.311</td><td>0.435</td><td>0.719</td><td>0.900</td><td>0.905</td><td>0.902</td><td>0.19</td></tr>
            <tr><td>id26</td><td>21.93</td><td>0.220</td><td>0.305</td><td>0.504</td><td>0.886</td><td>0.884</td><td>0.906</td><td>0.893</td><td>-0.03</td></tr>
            <tr><td>id20</td><td>24.17</td><td>0.240</td><td>0.297</td><td>0.498</td><td>0.682</td><td>0.914</td><td>0.918</td><td>0.915</td><td>0.21</td></tr>
            <tr><td>id13_11</td><td>23.26</td><td>0.229</td><td>0.288</td><td>0.485</td><td>0.680</td><td>0.907</td><td>0.906</td><td>0.906</td><td>0.03</td></tr>
            <tr><td>id13_4</td><td>23.33</td><td>0.227</td><td>0.276</td><td>0.471</td><td>0.660</td><td>0.899</td><td>0.903</td><td>0.900</td><td>0</td></tr>
            <tr><td>id13_1</td><td>22.72</td><td>0.222</td><td>0.276</td><td>0.466</td><td>0.662</td><td>0.897</td><td>0.903</td><td>0.899</td><td>-0.02</td></tr>
            <tr><td>id13</td><td>22.72</td><td>0.222</td><td>0.276</td><td>0.466</td><td>0.662</td><td>0.897</td><td>0.903</td><td>0.899</td><td>-0.02</td></tr>
            <tr><td>id13_6</td><td>23.42</td><td>0.228</td><td>0.275</td><td>0.469</td><td>0.656</td><td>0.895</td><td>0.903</td><td>0.898</td><td>-0.02</td></tr>
            <tr><td>id13_3</td><td>24.32</td><td>0.240</td><td>0.275</td><td>0.469</td><td>0.643</td><td>0.902</td><td>0.905</td><td>0.902</td><td>-0.03</td></tr>
            <tr><td>id13_2</td><td>22.84</td><td>0.224</td><td>0.273</td><td>0.465</td><td>0.664</td><td>0.899</td><td>0.903</td><td>0.900</td><td>-0.05</td></tr>
            <tr><td>id13_7</td><td>21.02</td><td>0.205</td><td>0.265</td><td>0.452</td><td>0.672</td><td>0.896</td><td>0.898</td><td>0.896</td><td>-0.06</td></tr>
            <tr><td>id13_8</td><td>21.84</td><td>0.214</td><td>0.263</td><td>0.451</td><td>0.666</td><td>0.896</td><td>0.898</td><td>0.896</td><td>-0.04</td></tr>
            <tr><td>id13_9</td><td>11.17</td><td>0.107</td><td>0.162</td><td>0.310</td><td>0.777</td><td>0.861</td><td>0.861</td><td>0.860</td><td>-0.64</td></tr>
            <tr><td>id13_10</td><td>12.45</td><td>0.109</td><td>0.156</td><td>0.298</td><td>0.759</td><td>0.868</td><td>0.861</td><td>0.864</td><td>-0.63</td></tr>
            <tr><td>id17_1</td><td>7.87</td><td>0.078</td><td>0.138</td><td>0.288</td><td>0.851</td><td>0.841</td><td>0.855</td><td>0.847</td><td>-0.87</td></tr>
            <tr><td>id31_1</td><td>7.87</td><td>0.078</td><td>0.138</td><td>0.288</td><td>0.851</td><td>0.841</td><td>0.855</td><td>0.847</td><td>-0.87</td></tr>
            <tr><td>id13_5</td><td>7.23</td><td>0.073</td><td>0.132</td><td>0.269</td><td>0.860</td><td>0.846</td><td>0.853</td><td>0.849</td><td>-0.82</td></tr>
            </table>
        <h4>Unseen Entities</h4>
            <table class="table table-hover table-condensed">
            <tr><th>SYSTEM ID</th><th>BLEU</th><th>BLEU_NLTK</th><th>METEOR</th><th>CHRF++</th><th>TER</th><th>BERT_PRECISION</th><th>BERT_RECALL</th><th>BERT_F1</th><th>BLEURT</th></tr>
            <tr><td>id30</td><td>52.37</td><td>0.520</td><td>0.416</td><td>0.694</td><td>0.398</td><td>0.963</td><td>0.960</td><td>0.961</td><td>0.65</td></tr>
            <tr><td>id23</td><td>52.76</td><td>0.523</td><td>0.415</td><td>0.691</td><td>0.381</td><td>0.964</td><td>0.961</td><td>0.962</td><td>0.67</td></tr>
            <tr><td>id5</td><td>50.75</td><td>0.505</td><td>0.415</td><td>0.687</td><td>0.411</td><td>0.961</td><td>0.959</td><td>0.959</td><td>0.65</td></tr>
            <tr><td>id30_1</td><td>50.59</td><td>0.504</td><td>0.414</td><td>0.689</td><td>0.427</td><td>0.960</td><td>0.959</td><td>0.959</td><td>0.62</td></tr>
            <tr><td>id34</td><td>50.27</td><td>0.497</td><td>0.414</td><td>0.689</td><td>0.411</td><td>0.962</td><td>0.961</td><td>0.961</td><td>0.65</td></tr>
            <tr><td>id36_1</td><td>50.27</td><td>0.497</td><td>0.414</td><td>0.689</td><td>0.411</td><td>0.962</td><td>0.961</td><td>0.961</td><td>0.65</td></tr>
            <tr><td>id35_1</td><td>50.27</td><td>0.497</td><td>0.414</td><td>0.689</td><td>0.411</td><td>0.962</td><td>0.961</td><td>0.961</td><td>0.65</td></tr>
            <tr><td>id18</td><td>52.25</td><td>0.517</td><td>0.413</td><td>0.691</td><td>0.394</td><td>0.963</td><td>0.960</td><td>0.961</td><td>0.65</td></tr>
            <tr><td>id36</td><td>49.88</td><td>0.491</td><td>0.411</td><td>0.685</td><td>0.413</td><td>0.962</td><td>0.961</td><td>0.961</td><td>0.65</td></tr>
            <tr><td>id35</td><td>49.88</td><td>0.491</td><td>0.411</td><td>0.685</td><td>0.413</td><td>0.962</td><td>0.961</td><td>0.961</td><td>0.65</td></tr>
            <tr><td>id2</td><td>51.23</td><td>0.500</td><td>0.406</td><td>0.687</td><td>0.417</td><td>0.960</td><td>0.958</td><td>0.959</td><td>0.63</td></tr>
            <tr><td>id4</td><td>50.74</td><td>0.504</td><td>0.405</td><td>0.672</td><td>0.410</td><td>0.958</td><td>0.956</td><td>0.957</td><td>0.61</td></tr>
            <tr><td>id26</td><td>49.4</td><td>0.490</td><td>0.398</td><td>0.669</td><td>0.410</td><td>0.961</td><td>0.958</td><td>0.959</td><td>0.64</td></tr>
            <tr><td>id26_1</td><td>49.4</td><td>0.490</td><td>0.398</td><td>0.669</td><td>0.410</td><td>0.961</td><td>0.958</td><td>0.959</td><td>0.64</td></tr>
            <tr><td>id11</td><td>44.08</td><td>0.427</td><td>0.395</td><td>0.659</td><td>0.469</td><td>0.949</td><td>0.949</td><td>0.948</td><td>0.52</td></tr>
            <tr><td>id12</td><td>43.46</td><td>0.422</td><td>0.395</td><td>0.658</td><td>0.464</td><td>0.950</td><td>0.950</td><td>0.949</td><td>0.53</td></tr>
            <tr><td>id15</td><td>41.12</td><td>0.409</td><td>0.391</td><td>0.655</td><td>0.493</td><td>0.947</td><td>0.952</td><td>0.949</td><td>0.52</td></tr>
            <tr><td>id16</td><td>41.12</td><td>0.409</td><td>0.391</td><td>0.655</td><td>0.493</td><td>0.947</td><td>0.952</td><td>0.949</td><td>0.52</td></tr>
            <tr><td>id28</td><td>46.63</td><td>0.445</td><td>0.390</td><td>0.653</td><td>0.448</td><td>0.956</td><td>0.954</td><td>0.955</td><td>0.6</td></tr>
            <tr><td>baseline</td><td>40.22</td><td>0.393</td><td>0.384</td><td>0.648</td><td>0.476</td><td>0.949</td><td>0.950</td><td>0.949</td><td>0.55</td></tr>
            <tr><td>id17</td><td>42.42</td><td>0.411</td><td>0.375</td><td>0.631</td><td>0.487</td><td>0.944</td><td>0.944</td><td>0.944</td><td>0.48</td></tr>
            <tr><td>id31</td><td>42.42</td><td>0.411</td><td>0.375</td><td>0.631</td><td>0.487</td><td>0.944</td><td>0.944</td><td>0.944</td><td>0.48</td></tr>
            <tr><td>id31_2</td><td>42.42</td><td>0.411</td><td>0.375</td><td>0.631</td><td>0.487</td><td>0.944</td><td>0.944</td><td>0.944</td><td>0.48</td></tr>
            <tr><td>id21</td><td>21.93</td><td>0.215</td><td>0.340</td><td>0.509</td><td>0.671</td><td>0.914</td><td>0.919</td><td>0.916</td><td>0.42</td></tr>
            <tr><td>id13_11</td><td>35.77</td><td>0.354</td><td>0.326</td><td>0.565</td><td>0.590</td><td>0.929</td><td>0.913</td><td>0.920</td><td>0.26</td></tr>
            <tr><td>id24</td><td>35.34</td><td>0.338</td><td>0.324</td><td>0.569</td><td>0.570</td><td>0.937</td><td>0.929</td><td>0.932</td><td>0.4</td></tr>
            <tr><td>id14</td><td>35.34</td><td>0.338</td><td>0.324</td><td>0.569</td><td>0.570</td><td>0.937</td><td>0.929</td><td>0.932</td><td>0.4</td></tr>
            <tr><td>id10</td><td>19.4</td><td>0.178</td><td>0.318</td><td>0.523</td><td>0.681</td><td>0.908</td><td>0.907</td><td>0.907</td><td>-0.01</td></tr>
            <tr><td>id13_3</td><td>33.97</td><td>0.338</td><td>0.316</td><td>0.543</td><td>0.583</td><td>0.924</td><td>0.913</td><td>0.917</td><td>0.17</td></tr>
            <tr><td>id13_1</td><td>32.69</td><td>0.324</td><td>0.315</td><td>0.542</td><td>0.599</td><td>0.920</td><td>0.910</td><td>0.915</td><td>0.19</td></tr>
            <tr><td>id13</td><td>32.69</td><td>0.324</td><td>0.315</td><td>0.542</td><td>0.599</td><td>0.920</td><td>0.910</td><td>0.915</td><td>0.19</td></tr>
            <tr><td>id13_4</td><td>34.04</td><td>0.340</td><td>0.315</td><td>0.544</td><td>0.586</td><td>0.923</td><td>0.912</td><td>0.917</td><td>0.21</td></tr>
            <tr><td>id13_2</td><td>31.99</td><td>0.318</td><td>0.312</td><td>0.537</td><td>0.616</td><td>0.919</td><td>0.910</td><td>0.914</td><td>0.16</td></tr>
            <tr><td>id13_6</td><td>32.33</td><td>0.320</td><td>0.310</td><td>0.535</td><td>0.614</td><td>0.916</td><td>0.908</td><td>0.911</td><td>0.18</td></tr>
            <tr><td>id20</td><td>26.04</td><td>0.249</td><td>0.306</td><td>0.533</td><td>0.709</td><td>0.921</td><td>0.924</td><td>0.921</td><td>0.31</td></tr>
            <tr><td>id26</td><td>25.95</td><td>0.253</td><td>0.302</td><td>0.521</td><td>0.857</td><td>0.896</td><td>0.906</td><td>0.900</td><td>0.07</td></tr>
            <tr><td>id13_7</td><td>29.16</td><td>0.288</td><td>0.301</td><td>0.518</td><td>0.651</td><td>0.913</td><td>0.904</td><td>0.908</td><td>0.1</td></tr>
            <tr><td>id13_8</td><td>28.83</td><td>0.284</td><td>0.295</td><td>0.509</td><td>0.648</td><td>0.911</td><td>0.902</td><td>0.905</td><td>0.05</td></tr>
            <tr><td>id13_9</td><td>12.9</td><td>0.113</td><td>0.167</td><td>0.319</td><td>0.792</td><td>0.871</td><td>0.854</td><td>0.862</td><td>-0.62</td></tr>
            <tr><td>id17_1</td><td>10.82</td><td>0.108</td><td>0.166</td><td>0.335</td><td>0.825</td><td>0.848</td><td>0.855</td><td>0.851</td><td>-0.71</td></tr>
            <tr><td>id31_1</td><td>10.82</td><td>0.108</td><td>0.166</td><td>0.335</td><td>0.825</td><td>0.848</td><td>0.855</td><td>0.851</td><td>-0.71</td></tr>
            <tr><td>id13_10</td><td>11.94</td><td>0.097</td><td>0.156</td><td>0.295</td><td>0.788</td><td>0.873</td><td>0.851</td><td>0.861</td><td>-0.68</td></tr>
            <tr><td>id13_5</td><td>9.72</td><td>0.096</td><td>0.154</td><td>0.302</td><td>0.877</td><td>0.844</td><td>0.843</td><td>0.843</td><td>-0.92
            </table>
      </div>
      <h3>Russian</h3>
      <div class="tab-pane show active" id="rdf2text-ru" role="tabpanel" aria-labelledby="rdf2text-ru-tab">
        <h4>All</h4>
            <table class="table table-hover table-condensed">
            <tr><th>SYSTEM ID</th><th>BLEU</th><th>BLEU_NLTK</th><th>METEOR</th><th>CHRF++</th><th>TER</th><th>BERT_PRECISION</th><th>BERT_RECALL</th><th>BERT_F1</th></tr>
            <tr><td>id6</td><td>51.63</td><td>0.521</td><td>0.676</td><td>0.683</td><td>0.420</td><td>0.909</td><td>0.907</td><td>0.907</td></tr>
            <tr><td>id3</td><td>52.93</td><td>0.532</td><td>0.672</td><td>0.677</td><td>0.398</td><td>0.914</td><td>0.905</td><td>0.909</td></tr>
            <tr><td>id25</td><td>46.84</td><td>0.468</td><td>0.632</td><td>0.637</td><td>0.456</td><td>0.899</td><td>0.890</td><td>0.893</td></tr>
            <tr><td>id32</td><td>46.84</td><td>0.468</td><td>0.632</td><td>0.637</td><td>0.456</td><td>0.899</td><td>0.890</td><td>0.893</td></tr>
            <tr><td>id38</td><td>45.28</td><td>0.451</td><td>0.617</td><td>0.641</td><td>0.452</td><td>0.903</td><td>0.894</td><td>0.898</td></tr>
            <tr><td>id37</td><td>45.28</td><td>0.451</td><td>0.617</td><td>0.641</td><td>0.452</td><td>0.903</td><td>0.894</td><td>0.898</td></tr>
            <tr><td>id27</td><td>47.29</td><td>0.477</td><td>0.616</td><td>0.622</td><td>0.453</td><td>0.897</td><td>0.882</td><td>0.888</td></tr>
            <tr><td>id27_1</td><td>46.92</td><td>0.473</td><td>0.612</td><td>0.620</td><td>0.461</td><td>0.895</td><td>0.879</td><td>0.886</td></tr>
            <tr><td>id32_1</td><td>41.52</td><td>0.418</td><td>0.602</td><td>0.610</td><td>0.486</td><td>0.891</td><td>0.883</td><td>0.886</td></tr>
            <tr><td>id9</td><td>43.1</td><td>0.430</td><td>0.576</td><td>0.595</td><td>0.487</td><td>0.898</td><td>0.873</td><td>0.884</td></tr>
            <tr><td>id38</td><td>41.99</td><td>0.417</td><td>0.576</td><td>0.598</td><td>0.517</td><td>0.883</td><td>0.866</td><td>0.874</td></tr>
            <tr><td>id25_1</td><td>24.87</td><td>0.251</td><td>0.523</td><td>0.537</td><td>0.673</td><td>0.849</td><td>0.855</td><td>0.851</td></tr>
            <tr><td>id32_2</td><td>24.87</td><td>0.251</td><td>0.523</td><td>0.537</td><td>0.673</td><td>0.849</td><td>0.855</td><td>0.851</td></tr>
            </table>

      </div>
    </div>


    <h2>WebNLG Text2RDF</h2>
    <h3>English</h3>
    <div class="tab-content" id="myTabContent">
      <div class="tab-pane show active" id="text2rdf-en" role="tabpanel" aria-labelledby="text2rdf-en-tab">
        <table class="table table-hover table-condensed">
            <tr>
                <th>System ID</th>
                <th>MATCH</th>
                <th>MACRO F-1</th>
                <th>MACRO PRECISION</th>
                <th>MACRO RECALL</th>
            </tr>
            <tr>
                <td>id19</td><td>Exact</td><td>0.6892</td><td>0.6889</td><td>0.6903</td>
            </tr>
            <tr>
                <td>id19</td><td>Ent_Type</td><td>0.7000</td><td>0.6993</td><td>0.7013</td>
            </tr>
            <tr>
                <td>id19</td><td>Partial</td><td>0.6964</td><td>0.6959</td><td>0.6977</td>
            </tr>
            <tr>
                <td>id19</td><td>Strict</td><td>0.6864</td><td>0.6859</td><td>0.6874</td>
            </tr>
            <tr>
                </hr>
            </tr>
            <tr>
                <td>id29</td><td>Exact_match</td><td>0.3415</td><td>0.3381</td><td>0.3492</td>
            </tr>
            <tr>
                <td>id29</td><td>Ent_Type</td><td>0.3427</td><td>0.3346</td><td>0.3563</td>
            </tr>
            <tr>
                <td>id29</td><td>Partial</td><td>0.3603</td><td>0.3546</td><td>0.3721</td>
            </tr>
            <tr>
                <td>id29</td><td>Strict</td><td>0.3094</td><td>0.3059</td><td>0.3154</td>
            </tr>
            <tr>
                </hr>
            </tr>
            <tr>
                <td>id7</td><td></td><td>0.6819</td><td>0.6698</td><td>0.7015</td>
            </tr>
            <tr>
                <td>id7</td><td>Ent_Type</td><td>0.7371</td><td>0.7214</td><td>0.7620</td>
            </tr>
            <tr>
                <td>id7</td><td>Partial</td><td>0.7135</td><td>0.6995</td><td>0.7359</td>
            </tr>
            <tr>
                <td>id7</td><td>Strict</td><td>0.6755</td><td>0.6635</td><td>0.6949</td>
            </tr>
	    </table>
      </div>
      <h3>Russian</h3>
      <div class="tab-pane  show active" id="rdf2text-ru" role="tabpanel" aria-labelledby="rdf2text-ru-tab">
        <table class="table table-hover table-condensed ">
            <tr>
                <th>System ID</th>
                <th>MATCH</th>
                <th>MACRO F-1</th>
                <th>MACRO PRECISION</th>
                <th>MACRO RECALL</th>
            </tr>
            <tr>
                <td>id8</td><td>Exact_match</td><td>0.9110</td><td>0.9072</td><td>0.9172</td>
            </tr>
            <tr>
                <td>id8</td><td>Ent_Type</td><td>0.9231</td><td>0.9184</td><td>0.9304</td>
            </tr>
            <tr>
                <td>id8</td><td>Partial</td><td>0.9171</td><td>0.9129</td><td>0.9239</td>
            </tr>
            <tr>
                <td>id8</td><td>Strict</td><td>0.9109</td><td>0.907</td><td>0.917</td>
            </tr>
	    </table>
      </div>
    </div>

<script  type="text/javascript">
$('#text2rdf-en-tab a').on('click', function (e) {
  e.preventDefault()
  $(this).tab('show')
})
$('#text2rdf-ru-tab a').on('click', function (e) {
  e.preventDefault()
  $(this).tab('show')
})
</script>
</body>
